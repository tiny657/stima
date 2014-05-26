package com.it.main;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.server.ServerHandlerAdapter;

@Sharable
public class ServerHandler extends ServerHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        super.channelRead(ctx, msg);

        if (msg instanceof Command) {
            Command cmd = (Command) msg;
            logger.info("data received: {}", cmd.toString());
            ReferenceCountUtil.release(msg);
        }
    }
}