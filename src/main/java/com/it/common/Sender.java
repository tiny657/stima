package com.it.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.model.AllMember;
import com.it.model.Member;
import com.it.model.MemberList;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    public static boolean sendBroadcast(String targetCluster, String msg) {
        MemberList memberList = AllMember.getInstance().getClusters()
                .getMemberListIn(targetCluster);

        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        for (Member member : memberList.getRunningMembers()) {
            AllMember.getInstance().getMemberInfos().getChannelFuture(member)
                    .channel().writeAndFlush(byteBuf);
        }

        return true;
    }

    public static boolean sendAnycast(String targetCluster, String msg) {
        MemberList memberList = AllMember.getInstance().getClusters()
                .getMemberListIn(targetCluster);
        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        Member member = memberList.nextRunningMember();

        if (member == null) {
            logger.error("Send fail.  Because there is no member in {}",
                    targetCluster);
            return false;
        } else {
            AllMember.getInstance().getMemberInfos().getChannelFuture(member)
                    .channel().writeAndFlush(byteBuf);
        }

        return true;
    }

    public static boolean sendUnicast(String targetHost, int targetPort,
            String msg) {
        Member member = AllMember.getInstance().getMember(targetHost,
                targetPort);
        if (member == null) {
            logger.error("Send fail because member({}:{}) is not found.",
                    targetHost, targetPort);
            return false;
        }

        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        AllMember.getInstance().getMemberInfos().getChannelFuture(member)
                .channel().writeAndFlush(byteBuf);

        return true;
    }
}