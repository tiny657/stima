package com.it.sender;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.it.model.AllServer;
import com.it.model.Server;

public class AnycastSender {
    public static boolean send(String targetCategory, String message) {
        ByteBuf byteBuf = Unpooled.buffer(message.length());
        byteBuf.writeBytes(message.getBytes());
        Server server = AllServer.getInstance().getCategory(targetCategory)
                .randomRunningServer();
        server.getChannelFuture().channel().writeAndFlush(byteBuf);

        return true;
    }
}