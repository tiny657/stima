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

package com.it.server;

import java.util.Map;

import com.it.main.Stima;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.Client;
import com.it.command.Command;
import com.it.command.InfoCommand;
import com.it.command.StartCommand;
import com.it.command.StopCommand;
import com.it.common.MailSender;
import com.it.config.MemberConfig;
import com.it.domain.*;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class ServerHandlerAdapter extends ChannelHandlerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(ServerHandlerAdapter.class);
  private Clusters savedClusters = null;

  @Override
  public void channelActive(ChannelHandlerContext ctx) {}

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof StartCommand) {
      StartCommand cmd = (StartCommand) msg;
      handleStartCommand(cmd);
      if (AllMember.getInstance().me().isMaster()) {
        String subject = getSubject(cmd, "Member is added: ");
        String content = getContent(cmd);
        MailSender.getInstance()
            .send(MemberConfig.getInstance().getMonitorMail(), subject, content);
      }
    } else if (msg instanceof StopCommand) {
      StopCommand cmd = (StopCommand) msg;
      handleStopCommand(cmd);
      if (AllMember.getInstance().me().isMaster()) {
        String subject = getSubject(cmd, "Member is removed: ");
        String content = getContent(cmd);
        MailSender.getInstance()
            .send(MemberConfig.getInstance().getMonitorMail(), subject, content);
      }
    } else if (msg instanceof InfoCommand) {
      InfoCommand cmd = (InfoCommand) msg;
      handleInfoCommand(cmd);
    }

    ReferenceCountUtil.release(msg);
    logger.info(AllMember.getInstance().toString());
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  private void handleStartCommand(StartCommand cmd) {
    Member member =
        AllMember.getInstance().getMemberByClusterAndId(cmd.getMyCluster(), cmd.getMyId());
    if (savedClusters != null) {
      removeMembers(savedClusters);
      addMembers(savedClusters);
      logger.info("Applied the received properties.");
    }

    if (member != null) {
      member.setStatus(Status.RUNNING);
      logger.info("StartCommand was received from {}.{}.", cmd.getMyCluster(), cmd.getMyId());
    } else {
      logger.error("StartCommand was received from {}.{}.  But that isn't existed.",
          cmd.getMyCluster(), cmd.getMyId());
    }
  }

  private void handleStopCommand(StopCommand cmd) {
    Member member =
        AllMember.getInstance().getMemberByClusterAndId(cmd.getMyCluster(), cmd.getMyId());
    if (member != null) {
      member.setStatus(Status.STANDBY);
      logger.info("StopCommand was received from {}.{}", cmd.getMyCluster(), cmd.getMyId());
    } else {
      logger.error("StopCommand was received from {}.{}.  But that isn't existed.",
          cmd.getMyCluster(), cmd.getMyId());
    }
  }

  private void handleInfoCommand(InfoCommand cmd) {
    Clusters clusters = cmd.getClusters();
    Member receivedMember = clusters.findMe();

    // compare the received properties.
    if (AllMember.getInstance().me().isBefore(receivedMember)) {
      if (AllMember.getInstance().getClusters().equals(clusters)) {
        logger.info("Properties are same.");
        savedClusters = null;
      } else {
        logger.info("Properties are different.");
        savedClusters = clusters;
      }

      logger.info("InfoCommand was received. {}.", clusters.toString());
    } else {
      if (AllMember.getInstance().getClusters().equals(clusters)) {
        logger.info("Properties are same.");
      } else {
        logger
            .info(
                "Properties are different.  Stop this application in {} seconds if this properties don't spread to all members.",
                MemberConfig.getInstance().getSpreadTime());
      }
    }

    // update the status of the sender.
    Member receivedMemberInLocal =
        AllMember.getInstance().getMemberByDataPort(receivedMember.getHost(),
            receivedMember.getDataPort());
    receivedMemberInLocal.setStatus(receivedMember.getStatus());
    receivedMemberInLocal.setBootupTime(receivedMember.getBootupTime());
    receivedMemberInLocal.setDesc(receivedMember.getDesc());

    AllMember.getInstance().me().calculatePriorityPointWhenConnect(receivedMember);
  }

  private void removeMembers(Clusters clusters) {
    Map<String, MemberList> removedmember = AllMember.getInstance().getClusters().diff(clusters);
    logger.info("Member({}) are removed.", removedmember.toString());

    for (String cluster : removedmember.keySet()) {
      // remove clister
      if (AllMember.getInstance().getMemberListIn(cluster).size() == removedmember.get(cluster)
          .size()) {
        logger.info("Cluster({}) is removed.", cluster);
        AllMember.getInstance().removeCluster(cluster);
        MemberConfig.getInstance().removeCluster(cluster);
      }

      for (Member member : removedmember.get(cluster).getMembers()) {
        // stop the client thread
        AllMember.getInstance().getMemberInfos().getDataClient(member).interrupt();
        AllMember.getInstance().getMemberInfos().getControlClient(member).interrupt();

        // remove the client and the client info
        AllMember.getInstance().removeMember(cluster, member);
        AllMember.getInstance().getMemberInfos().removeInfo(member);
        MemberConfig.getInstance().removeMember(cluster, member);
      }
    }
  }

  private void addMembers(Clusters clusters) {
    Map<String, MemberList> addedMember = clusters.diff(AllMember.getInstance().getClusters());
    logger.info("Member({}) is added.", addedMember.toString());

    for (String cluster : addedMember.keySet()) {
      AllMember.getInstance().addCluster(cluster);
      for (Member member : addedMember.get(cluster).getMembers()) {
        // start the client
        Client dataClient = Stima.getInstance().createDataClient(member);
        Client controlClient = Stima.getInstance().createControlClient(member);

        // add the client data
        AllMember.getInstance().addMember(cluster, member);
        AllMember.getInstance().getMemberInfos().putDataClient(member, dataClient);
        AllMember.getInstance().getMemberInfos().putControlClient(member, controlClient);
        MemberConfig.getInstance().addMember(cluster, member);
      }
    }
  }

  private String getSubject(Command cmd, String subjectPrefix) {
    StringBuilder subject = new StringBuilder();
    subject.append(subjectPrefix).append(cmd.getMyCluster()).append(".").append(cmd.getMyId());
    return subject.toString();
  }

  private String getContent(Command cmd) {
    Member receivedMember =
        AllMember.getInstance().getMemberByClusterAndId(cmd.getMyCluster(), cmd.getMyId());
    return receivedMember.toString();
  }
}
