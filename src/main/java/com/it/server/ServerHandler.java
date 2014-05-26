package com.it.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.Client;
import com.it.common.Config;
import com.it.model.AllMember;
import com.it.model.Clusters;
import com.it.model.Member;
import com.it.model.MemberList;

public class ServerHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(Server.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Clusters) {
            // control packet
            try {
                Clusters categories = (Clusters) msg;
                if (AllMember.getInstance().getClusters().getBootupTime()
                        .compareTo(categories.getBootupTime()) < 0) {
                    if (AllMember.getInstance().getClusters()
                            .equals(categories)) {
                        logger.info("properties is same.");
                    } else {
                        logger.info("properties is different.");
                        removeMembers(categories);
                        addMembers(categories);
                    }

                    logger.info("server received: {}", categories.toString());
                } else {
                    logger.info("ignore the received properties because this server is started up late.");
                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
        } else {
            // data packet
            logger.info("data received: {}", msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void removeMembers(Clusters categories) {
        Map<String, MemberList> removedmember = AllMember.getInstance()
                .getClusters().diff(categories);
        logger.info("removed member : {}", removedmember.toString());

        for (String cluster : removedmember.keySet()) {
            // remove clister
            if (AllMember.getInstance().getMemberListIn(cluster).size() == removedmember
                    .get(cluster).size()) {
                logger.info("removed cluster: {}", cluster);
                AllMember.getInstance().removeCluster(cluster);
                Config.getInstance().removeCluster(cluster);
            }

            for (Member member : removedmember.get(cluster).getMembers()) {
                // stop client thread
                AllMember.getInstance().getMemberInfos().getClient(member)
                        .interrupt();

                // remove client and client info
                AllMember.getInstance().removeMember(cluster, member);
                AllMember.getInstance().getMemberInfos().removeInfo(member);
                Config.getInstance().removeMember(cluster, member);
            }
        }
    }

    private void addMembers(Clusters categories) {
        Map<String, MemberList> addedMember = categories.diff(AllMember
                .getInstance().getClusters());
        logger.info("added member : " + addedMember.toString());

        for (String cluster : addedMember.keySet()) {
            AllMember.getInstance().addCluster(cluster);
            for (Member member : addedMember.get(cluster).getMembers()) {
                // start client
                Client client = new Client(member);
                client.start();

                // add client data
                AllMember.getInstance().addMember(cluster, member);
                AllMember.getInstance().getMemberInfos().put(member, client);
                Config.getInstance().addMember(cluster, member);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}