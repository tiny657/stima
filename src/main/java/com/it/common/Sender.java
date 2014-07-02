package com.it.common;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.command.Command;
import com.it.model.AllMember;
import com.it.model.Member;
import com.it.model.MemberList;
import io.netty.channel.ChannelFuture;

public class Sender {
  private static final Logger logger = LoggerFactory.getLogger(Sender.class);

  // public static boolean sendBroadcast(String targetCluster, String msg) {
  // ByteBuf byteBuf = Unpooled.buffer(msg.length());
  // byteBuf.writeBytes(msg.getBytes());
  // MemberList memberList = AllMember.getInstance().getClusters()
  // .getMemberListIn(targetCluster);
  //
  // for (Member member : memberList.getRunningMembers()) {
  // AllMember.getInstance().getMemberInfos().getChannelFuture(member)
  // .channel().writeAndFlush(byteBuf);
  // }
  //
  // return true;
  // }

  public static boolean sendBroadcast(Object msg) {
    for (Entry<String, MemberList> entry : AllMember.getInstance().getClusters().getMemberListMap()
        .entrySet()) {
      for (Member member : entry.getValue().getMembers()) {
        if (member.isRunning() && !member.isMe()) {
          if (!(msg instanceof Command)) {
            member.increaseSentCount();
          }
          AllMember.getInstance().getMemberInfos().getChannelFuture(member).channel()
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
    } else {
      AllMember.getInstance().getMemberInfos().getChannelFuture(member).channel()
          .writeAndFlush(msg);
      if (!(msg instanceof Command)) {
        member.increaseSentCount();
      }
    }

    return true;
  }

  // public static boolean sendAnycast(String targetCluster, String msg) {
  // Member member = AllMember.getInstance().getClusters()
  // .getMemberListIn(targetCluster).nextRunningMember();
  // if (member == null) {
  // logger.error("Send fail.  Because there is no member in {}",
  // targetCluster);
  // return false;
  // } else {
  // ByteBuf byteBuf = Unpooled.buffer(msg.length());
  // byteBuf.writeBytes(msg.getBytes());
  // AllMember.getInstance().getMemberInfos().getChannelFuture(member)
  // .channel().writeAndFlush(byteBuf);
  // }
  //
  // return true;
  // }

  public static boolean sendUnicast(String targetCluster, int targetId, Object msg) {
    Member member =
        AllMember.getInstance().getClusters().getMemberListIn(targetCluster).getMember(targetId);

    if (member == null) {
      logger.error("Send fail.  Because there is no member in {}(id:{})", targetCluster, targetId);
      return false;
    } else {
      ChannelFuture channelFuture =
          AllMember.getInstance().getMemberInfos().getChannelFuture(member);
      if (channelFuture != null) {
        channelFuture.channel().writeAndFlush(msg);
        if (!(msg instanceof Command)) {
          member.increaseSentCount();
        }
      } else {
        logger.warn("Send fail.  Because the status of {}(id:{}) isn't running.", targetCluster,
            targetId);
      }
    }

    return true;
  }
}
