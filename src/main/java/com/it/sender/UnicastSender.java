package com.it.sender;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.it.model.AllServer;
import com.it.model.Server;

public class UnicastSender {
    public static boolean send(String targetHost, int targetPort, String message) {
        ByteBuf byteBuf = Unpooled.buffer(message.length());
        byteBuf.writeBytes(message.getBytes());
        Server server = AllServer.getInstance().getServer(targetHost,
                targetPort);
        server.getChannelFuture().channel().writeAndFlush(byteBuf);

        return true;
    }
}