package com.it.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.it.client.ItClient;
import com.it.common.Config;
import com.it.common.JsonUtils;
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
        try {
            ByteBuf in = (ByteBuf) msg;
            String content = in.toString(io.netty.util.CharsetUtil.US_ASCII);
            Categories categories = JsonUtils.fromJson(content,
                    Categories.class);
            if (AllServer.getInstance().getCategories().equals(categories)) {
                logger.info("server properties is same.");
            } else {
                logger.info("server properties is different.");
                checkServersToRemove(categories);
                checkServersToAdd(categories);
            }

            logger.info("server received: {}", categories.toString());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void checkServersToAdd(Categories categories) {
        Map<String, ServerList> addedServer = categories.diff(AllServer
                .getInstance().getCategories());
        logger.info("added server: " + addedServer.toString());

        for (String category : addedServer.keySet()) {
            AllServer.getInstance().addCategory(category);
            for (Server server : addedServer.get(category).getServers()) {
                // start client thread
                ItClient itClient = new ItClient(server.getHost(),
                        server.getPort());
                itClient.start();

                // add client data
                AllServer.getInstance().addServer(category, server);
                AllServer.getInstance().getServerInfo().add(server, itClient);
                Config.getInstance().addServer(category,
                        server.getHost() + ":" + server.getPort());
            }
        }
    }

    private void checkServersToRemove(Categories categories) {
        Map<String, ServerList> removedServer = AllServer.getInstance()
                .getCategories().diff(categories);
        logger.info("removed server: " + removedServer.toString());

        for (String category : removedServer.keySet()) {
            for (Server server : removedServer.get(category).getServers()) {
                // stop client thread
                ItClient itClient = AllServer.getInstance().getServerInfo()
                        .getItClient(server);
                itClient.interrupt();

                // remove client data
                AllServer.getInstance().removeServer(category, server);
                AllServer.getInstance().getServerInfo().remove(server);
                Config.getInstance().removeServer(category,
                        server.getHost() + ":" + server.getPort());
            }

            // remove category
            if (AllServer.getInstance().getServerListIn(category).getServers()
                    .size() == 0) {
                AllServer.getInstance().removeCategory(category);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}