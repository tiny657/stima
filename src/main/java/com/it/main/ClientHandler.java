package com.it.main;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.client.ClientHandlerAdapter;

@Sharable
public class ClientHandler extends ClientHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(ClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Command) {
            Command cmd = (Command) msg;
            logger.info("data received: {}", cmd.toString());
            ReferenceCountUtil.release(msg);
        }
    }
}