/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.it.main;

import java.io.FileNotFoundException;
import java.util.Set;

import com.google.common.collect.Sets;
import com.it.domain.*;
import com.it.exception.InvalidMemberException;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.Client;
import com.it.client.ClientHandlerAdapter;
import com.it.client.ControlClient;
import com.it.client.DataClient;
import com.it.command.StartCommand;
import com.it.command.StopCommand;
import com.it.common.ControlSender;
import com.it.config.JoptConfig;
import com.it.config.MailConfig;
import com.it.config.MemberConfig;
import com.it.job.JobManager;
import com.it.monitor.MonitorServer;
import com.it.server.ControlServer;
import com.it.server.DataServer;
import com.it.server.Server;
import com.it.server.ServerHandlerAdapter;
import io.netty.channel.ChannelFuture;

public class ItRunner {
  private static final Logger logger = LoggerFactory.getLogger(ItRunner.class);

  public static ItRunner instance = new ItRunner();
  private ServerHandlerAdapter serverHandlerAdapter;
  private ClientHandlerAdapter clientHandlerAdapter;

  public static ItRunner getInstance() {
    return instance;
  }

  public ServerHandlerAdapter getServerHandlerAdapter() {
    return serverHandlerAdapter;
  }

  public ClientHandlerAdapter getClientHandlerAdapter() {
    return clientHandlerAdapter;
  }

  public void execute(ServerHandlerAdapter serverHandlerAdapter,
      ClientHandlerAdapter clientHandlerAdapter, String[] args) {
    this.serverHandlerAdapter = serverHandlerAdapter;
    this.clientHandlerAdapter = clientHandlerAdapter;

    try {
      initialize(args);
      Clusters clusters = AllMember.getInstance().getClusters();

      // monitor
      MonitorServer monitorServer = new MonitorServer(MemberConfig.getInstance().getMonitorPort());
      monitorServer.start();

      // server
      Server dataServer = new DataServer(AllMember.getInstance().me());
      dataServer.setServerHandler(serverHandlerAdapter);
      dataServer.start();
      dataServer.await();

      Server controlServer = new ControlServer(AllMember.getInstance().me());
      controlServer.start();
      controlServer.await();

      // clients
      for (String clusterName : clusters.getClusterNames()) {
        for (Member member : clusters.getMemberListIn(clusterName).getMembers()) {
          if (!member.isMe()) {
            Client dataClient = new DataClient(member);
            dataClient.setClientHandler(clientHandlerAdapter);
            dataClient.start();
            dataClient.await();

            Client controlClient = new ControlClient(member);
            controlClient.start();
            controlClient.await();
          }
        }
      }

      // change status to Running
      Member myInfo = dataServer.getMyInfo();
      AllMember.getInstance().getMemberByDataPort(myInfo.getHost(), myInfo.getDataPort())
          .setStatus(Status.RUNNING);

      logger.info(AllMember.getInstance().toString());

      Thread.sleep(MemberConfig.getInstance().getSpreadTime() * 1000);

      // broadcast StartCommand
      ControlSender.sendBroadcast(new StartCommand(MemberConfig.getInstance().getMyCluster(),
          MemberConfig.getInstance().getMyId()));
    } catch (Exception e) {
      e.printStackTrace();
      shutdown();
    }
  }

  public void shutdown() {
    ControlSender.sendBroadcast(new StopCommand(MemberConfig.getInstance().getMyCluster(),
        MemberConfig.getInstance().getMyId()));

    try {
      // wait for 1000ms after sending StopCommand.
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // close client.
    Clusters clusters = AllMember.getInstance().getClusters();
    for (String clusterName : clusters.getClusterNames()) {
      for (Member member : clusters.getMemberListIn(clusterName).getMembers()) {
        if (!member.isMe()) {
          Client dataClient = AllMember.getInstance().getMemberInfos().getDataClient(member);
          if (dataClient != null) {
            dataClient.shutdown();
          }

          Client controlClient = AllMember.getInstance().getMemberInfos().getControlClient(member);
          if (controlClient != null) {
            controlClient.shutdown();
          }
        }
      }
    }

    // close server.
    Member me = AllMember.getInstance().me();
    ChannelFuture dataChannelFuture =
        AllMember.getInstance().getMemberInfos().getDataChannelFuture(me);
    if (dataChannelFuture != null) {
      dataChannelFuture.channel().close();
    }
    ChannelFuture controlChannelFuture =
        AllMember.getInstance().getMemberInfos().getControlChannelFuture(me);
    if (controlChannelFuture != null) {
      controlChannelFuture.channel().close();
    }
  }

  private void initialize(String[] args) throws ConfigurationException, FileNotFoundException,
      InvalidMemberException {
    // initialize config
    JoptConfig.getInstance().init(args);
    MemberConfig.getInstance().init(args);
    MailConfig.getInstance().init(args);

    validateIpAndPort();

    // monitor
    JobManager.getInstance().runCollectorJob();
  }

  private boolean validateIpAndPort() throws InvalidMemberException {
    Clusters clusters = AllMember.getInstance().getClusters();
    Set<String> uniqueIpPort = Sets.newHashSet();
    for (MemberList memberList : clusters.getMemberListMap().values()) {
      for (Member member : memberList.getMembers()) {
        if (!uniqueIpPort.add(member.getHost() + ":" + member.getDataPort())) {
          throw new InvalidMemberException("Member is duplicated. " + member.getHost() + ":"
              + member.getDataPort());
        }
        if (!uniqueIpPort.add(member.getHost() + ":" + member.getControlPort())) {
          throw new InvalidMemberException("Member is duplicated. " + member.getHost() + ":"
              + member.getControlPort());
        }
      }
    }
    return true;
  }
}
