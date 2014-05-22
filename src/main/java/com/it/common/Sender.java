package com.it.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.model.AllServer;
import com.it.model.Server;
import com.it.model.ServerList;

public class Sender {
    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    public static boolean sendBroadcast(String targetCategory, String msg) {
        ServerList serverList = AllServer.getInstance().getCategories()
                .getServerListIn(targetCategory);
        if (serverList.hasServers()) {
            logger.error("Send fail.  Because there is no server in {}",
                    targetCategory);
            return false;
        }

        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        for (Server server : serverList.getRunningServers()) {
            AllServer.getInstance().getServerInfos().getChannelFuture(server)
                    .channel().writeAndFlush(byteBuf);
        }

        return true;
    }

    public static boolean sendAnycast(String targetCategory, String msg) {
        ServerList serverList = AllServer.getInstance().getCategories()
                .getServerListIn(targetCategory);
        if (!serverList.hasServers()) {
            logger.error("Send fail.  Because there is no server in {}",
                    targetCategory);
            return false;
        }

        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        Server server = serverList.randomRunningServer();
        AllServer.getInstance().getServerInfos().getChannelFuture(server)
                .channel().writeAndFlush(byteBuf);

        return true;
    }

    public static boolean sendUnicast(String targetHost, int targetPort,
            String msg) {
        Server server = AllServer.getInstance().getServer(targetHost,
                targetPort);
        if (server == null) {
            logger.error("Send fail because server({}:{}) is not found.",
                    targetHost, targetPort);
            return false;
        }

        ByteBuf byteBuf = Unpooled.buffer(msg.length());
        byteBuf.writeBytes(msg.getBytes());
        AllServer.getInstance().getServerInfos().getChannelFuture(server)
                .channel().writeAndFlush(byteBuf);

        return true;
    }
}