package com.it.sender;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.it.model.AllServer;
import com.it.model.Server;

public class BroadcastSender {
    public static boolean send(String targetCategory, String message) {
        System.out.println(message);
        ByteBuf byteBuf = Unpooled.buffer(message.length());
        byteBuf.writeBytes(message.getBytes());
        for (Server server : AllServer.getInstance()
                .getCategory(targetCategory).getRunningServers()) {
            server.getChannelFuture().channel().writeAndFlush(byteBuf);
        }

        return true;
    }
}