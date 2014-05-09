package com.it.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.model.AllServer;
import com.it.model.Server;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    public static boolean sendBroadcast(String targetCategory, String msg) {
        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        for (Server server : AllServer.getInstance()
                .getCategory(targetCategory).getRunningServers()) {
            AllServer.getInstance().getServerInfo().getChannelFuture(server)
                    .channel().writeAndFlush(byteBuf);
        }

        return true;
    }

    public static boolean sendAnycast(String targetCategory, String msg) {
        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        Server server = AllServer.getInstance().getCategory(targetCategory)
                .randomRunningServer();
        if (server != null) {
            AllServer.getInstance().getServerInfo().getChannelFuture(server)
                    .channel().writeAndFlush(byteBuf);
        } else {
            logger.info("No server in category({})", targetCategory);
        }

        return true;
    }

    public static boolean sendUnicast(String targetHost, int targetPort,
            String msg) {
        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        Server server = AllServer.getInstance().getServer(targetHost,
                targetPort);
        if (server != null) {
            AllServer.getInstance().getServerInfo().getChannelFuture(server)
                    .channel().writeAndFlush(byteBuf);
        }

        return true;
    }
}