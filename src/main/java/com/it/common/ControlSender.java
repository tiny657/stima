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

package com.it.common;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.command.Command;
import com.it.domain.AllMember;
import com.it.domain.Member;
import com.it.domain.MemberList;
import io.netty.channel.ChannelFuture;

public class ControlSender {
  private static final Logger logger = LoggerFactory.getLogger(ControlSender.class);

  public static boolean sendBroadcast(Object msg) {
    for (Entry<String, MemberList> entry : AllMember.getInstance().getClusters().getMemberListMap()
        .entrySet()) {
      for (Member member : entry.getValue().getMembers()) {
        if (member.isRunning() && !member.isMe()) {
          AllMember.getInstance().getMemberInfos().getControlChannelFuture(member).channel()
              .writeAndFlush(msg);
        }
      }
    }

    return true;
  }

  public static boolean sendAnycast(String targetCluster, Object msg) {
    Member member =
        AllMember.getInstance().getClusters().getMemberListIn(targetCluster).nextRunningMember();

    if (member == null) {
      logger.error("Send fail.  Because there is no member in {}", targetCluster);
      return false;
    }

    AllMember.getInstance().getMemberInfos().getControlChannelFuture(member).channel()
        .writeAndFlush(msg);

    return true;
  }

  public static boolean sendUnicast(String targetCluster, int targetId, Object msg) {
    Member member =
        AllMember.getInstance().getClusters().getMemberListIn(targetCluster).getMember(targetId);

    if (member == null) {
      logger.error("Send fail.  Because there is no member in {}(id:{})", targetCluster, targetId);
      return false;
    }

    ChannelFuture channelFuture =
        AllMember.getInstance().getMemberInfos().getControlChannelFuture(member);
    if (channelFuture != null) {
      channelFuture.channel().writeAndFlush(msg);
    } else {
      logger.warn("Send fail.  Because the status of {}(id:{}) isn't running.", targetCluster,
          targetId);
    }

    return true;
  }
}
