package com.it.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                Map<String, ServerList> addedServer = AllServer.getInstance()
                        .getCategories().addedServerFrom(categories);
                logger.info("added server: " + addedServer.toString());

                // add properties file
                for (String category : addedServer.keySet()) {
                    AllServer.getInstance().addCategory(category);
                    for (Server server : addedServer.get(category).getServers()) {
                        AllServer.getInstance().addServer(category, server);
                        Config.getInstance().addServer(category,
                                server.getHost() + ":" + server.getPort());

                        // start client thread
                        new Thread(new ItClient(server.getHost(),
                                server.getPort())).start();
                    }
                }
            }

            logger.info("server received: {}", categories.toString());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}