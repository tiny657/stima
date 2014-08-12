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
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.it.client.Client;
import com.it.client.ControlClient;
import com.it.client.DataClient;
import com.it.command.StartCommand;
import com.it.command.StopCommand;
import com.it.common.ControlSender;
import com.it.common.HandlerType;
import com.it.config.JoptConfig;
import com.it.config.MailConfig;
import com.it.config.MemberConfig;
import com.it.domain.*;
import com.it.exception.InvalidMemberException;
import com.it.job.JobManager;
import com.it.monitor.MonitorServer;
import com.it.server.ControlServer;
import com.it.server.DataServer;
import com.it.server.Server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

public class Stima {
  private static final Logger logger = LoggerFactory.getLogger(Stima.class);

  public static Stima instance;
  private HandlerType handlerType;
  private ChannelHandler serverHandler;
  private ChannelHandler clientHandler;
  private String[] args;

  private Stima(Builder builder) {
    handlerType = builder.getType();
    serverHandler = builder.getServerhandler();
    clientHandler = builder.getClientHandler();
    args = Arrays.copyOf(builder.args, builder.args.length);
  }

  public static Stima getInstance() {
    return instance;
  }

  public void start() {
    try {
      initialize(args);
      Clusters clusters = AllMember.getInstance().getClusters();

      // monitor
      MonitorServer monitorServer = new MonitorServer(MemberConfig.getInstance().getMonitorPort());
      monitorServer.start();

      // server
      Server dataServer = new DataServer(AllMember.getInstance().me());
      dataServer.setHandlerType(handlerType);
      dataServer.setHandler(serverHandler);
      dataServer.start();
      dataServer.awaitConnection();

      Server controlServer = new ControlServer(AllMember.getInstance().me());
      controlServer.start();
      controlServer.awaitConnection();

      // clients
      for (String clusterName : clusters.getClusterNames()) {
        for (Member member : clusters.getMemberListIn(clusterName).getMembers()) {
          if (!member.isMe()) {
            createDataClient(member);
            createControlClient(member);
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

  public Client createDataClient(Member member) {
    Client client = new DataClient(member);
    client.setHandlerType(handlerType);
    client.setHandler(clientHandler);
    client.start();
    client.awaitConnection();
    return client;
  }

  public Client createControlClient(Member member) {
    Client client = new ControlClient(member);
    client.start();
    client.awaitConnection();
    return client;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    HandlerType handlerType;
    ChannelHandler serverHandler;
    ChannelHandler clientHandler;
    String[] args;

    public void handlerType(HandlerType handlerType) {
      this.handlerType = handlerType;
    }

    public HandlerType getType() {
      return handlerType;
    }

    public Builder serverHandler(ChannelHandler handler) {
      serverHandler = handler;
      return this;
    }

    public ChannelHandler getServerhandler() {
      return serverHandler;
    }

    public Builder clientHandler(ChannelHandler handler) {
      clientHandler = handler;
      return this;
    }

    public ChannelHandler getClientHandler() {
      return clientHandler;
    }

    public Builder args(String[] args) {
      this.args = Arrays.copyOf(args, args.length);
      return this;
    }

    public Stima build() {
      return new Stima(this);
    }
  }

  private void initialize(String[] args) throws ConfigurationException, FileNotFoundException,
      InvalidMemberException {
    // initialize config
    JoptConfig.getInstance().init(args);
    MemberConfig.getInstance().init();
    MailConfig.getInstance().init();

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
