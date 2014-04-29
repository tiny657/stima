package com.it.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class ServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("send initial messages");
        ByteBuf messages = Unpooled.buffer(50);
        for (int i = 0; i < messages.capacity(); i++) {
            messages.writeByte((byte) i);
        }

        ctx.writeAndFlush(messages);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
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