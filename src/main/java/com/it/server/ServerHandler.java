package com.it.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.ItClient;
import com.it.common.Config;
import com.it.model.AllServer;
import com.it.model.Categories;
import com.it.model.Server;
import com.it.model.ServerList;

public class ServerHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(ItServer.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Categories) {
            // control packet
            try {
                Categories categories = (Categories) msg;
                if (AllServer.getInstance().getCategories().getBootupTime()
                        .compareTo(categories.getBootupTime()) < 0) {
                    if (AllServer.getInstance().getCategories()
                            .equals(categories)) {
                        logger.info("properties is same.");
                    } else {
                        logger.info("properties is different.");
                        removeServers(categories);
                        addServers(categories);
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

    private void removeServers(Categories categories) {
        Map<String, ServerList> removedServer = AllServer.getInstance()
                .getCategories().diff(categories);
        logger.info("removed server: {}", removedServer.toString());

        for (String category : removedServer.keySet()) {
            // remove category
            if (AllServer.getInstance().getServerListIn(category).size() == removedServer
                    .get(category).size()) {
                logger.info("removed category: {}", category);
                AllServer.getInstance().removeCategory(category);
                Config.getInstance().removeCategory(category);
            }

            for (Server server : removedServer.get(category).getServers()) {
                // stop client thread
                AllServer.getInstance().getServerInfos().getItClient(server)
                        .interrupt();

                // remove client and client info
                AllServer.getInstance().removeServer(category, server);
                AllServer.getInstance().getServerInfos().removeInfo(server);
                Config.getInstance().removeServer(category, server);
            }
        }
    }

    private void addServers(Categories categories) {
        Map<String, ServerList> addedServer = categories.diff(AllServer
                .getInstance().getCategories());
        logger.info("added server: " + addedServer.toString());

        for (String category : addedServer.keySet()) {
            AllServer.getInstance().addCategory(category);
            for (Server server : addedServer.get(category).getServers()) {
                // start client
                ItClient itClient = new ItClient(server);
                itClient.start();

                // add client data
                AllServer.getInstance().addServer(category, server);
                AllServer.getInstance().getServerInfos().put(server, itClient);
                Config.getInstance().addServer(category, server);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}