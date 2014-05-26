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
import com.it.model.Categories;
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
        if (msg instanceof Categories) {
            // control packet
            try {
                Categories categories = (Categories) msg;
                if (AllMember.getInstance().getCategories().getBootupTime()
                        .compareTo(categories.getBootupTime()) < 0) {
                    if (AllMember.getInstance().getCategories()
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

    private void removeMembers(Categories categories) {
        Map<String, MemberList> removedmember = AllMember.getInstance()
                .getCategories().diff(categories);
        logger.info("removed member : {}", removedmember.toString());

        for (String category : removedmember.keySet()) {
            // remove category
            if (AllMember.getInstance().getMemberListIn(category).size() == removedmember
                    .get(category).size()) {
                logger.info("removed category: {}", category);
                AllMember.getInstance().removeCategory(category);
                Config.getInstance().removeCategory(category);
            }

            for (Member member : removedmember.get(category).getMembers()) {
                // stop client thread
                AllMember.getInstance().getMemberInfos().getClient(member)
                        .interrupt();

                // remove client and client info
                AllMember.getInstance().removeMember(category, member);
                AllMember.getInstance().getMemberInfos().removeInfo(member);
                Config.getInstance().removeMember(category, member);
            }
        }
    }

    private void addMembers(Categories categories) {
        Map<String, MemberList> addedMember = categories.diff(AllMember
                .getInstance().getCategories());
        logger.info("added member : " + addedMember.toString());

        for (String category : addedMember.keySet()) {
            AllMember.getInstance().addCategory(category);
            for (Member member : addedMember.get(category).getMembers()) {
                // start client
                Client client = new Client(member);
                client.start();

                // add client data
                AllMember.getInstance().addMember(category, member);
                AllMember.getInstance().getMemberInfos().put(member, client);
                Config.getInstance().addMember(category, member);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}