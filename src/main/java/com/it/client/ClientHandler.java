package com.it.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.Config;
import com.it.model.AllMember;

public class ClientHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(Client.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (Config.getInstance().isAutoSpread()) {
            ctx.channel()
                    .writeAndFlush(AllMember.getInstance().getCategories());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            String received = in.toString(io.netty.util.CharsetUtil.US_ASCII);
            logger.info("data received: {}", received);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}