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
          if (!(msg instanceof Command)) {
            member.increaseSentCount();
          }
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
    if (!(msg instanceof Command)) {
      member.increaseSentCount();
    }

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
      if (!(msg instanceof Command)) {
        member.increaseSentCount();
      }
    } else {
      logger.warn("Send fail.  Because the status of {}(id:{}) isn't running.", targetCluster,
          targetId);
    }

    return true;
  }
}