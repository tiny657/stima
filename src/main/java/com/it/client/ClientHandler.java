package com.it.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.Config;
import com.it.common.JsonUtils;
import com.it.model.AllServer;

public class ClientHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(ItClient.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (Config.getInstance().isAutoSpread()) {
            String json = JsonUtils.toJson(AllServer.getInstance()
                    .getCategories());
            ByteBuf byteBuf = Unpooled.buffer(json.length());
            byteBuf.writeBytes(json.getBytes());
            ctx.channel().writeAndFlush(byteBuf);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            logger.info("server received: {}",
                    in.toString(io.netty.util.CharsetUtil.US_ASCII));
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