package com.it.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.it.model.AllServer;
import com.it.model.Server;

public class Sender {
    public static boolean sendBroadcast(String targetCategory, String message) {
        ByteBuf byteBuf = Unpooled.buffer(message.length());
        byteBuf.writeBytes(message.getBytes());
        System.out.println(AllServer.getInstance().getCategory(targetCategory)
                .getRunningServers().toString());
        for (Server server : AllServer.getInstance()
                .getCategory(targetCategory).getRunningServers()) {
            server.getChannelFuture().channel().writeAndFlush(byteBuf);
        }

        return true;
    }

    public static boolean sendAnycast(String targetCategory, String message) {
        ByteBuf byteBuf = Unpooled.buffer(message.length());
        byteBuf.writeBytes(message.getBytes());
        Server server = AllServer.getInstance().getCategory(targetCategory)
                .randomRunningServer();
        server.getChannelFuture().channel().writeAndFlush(byteBuf);

        return true;
    }

    public static boolean sendUnicast(String targetHost, int targetPort,
            String message) {
        ByteBuf byteBuf = Unpooled.buffer(message.length());
        byteBuf.writeBytes(message.getBytes());
        Server server = AllServer.getInstance().getServer(targetHost,
                targetPort);
        server.getChannelFuture().channel().writeAndFlush(byteBuf);

        return true;
    }
}