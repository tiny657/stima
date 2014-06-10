package com.it.client;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.command.InfoCommand;
import com.it.config.MemberConfig;
import com.it.model.AllMember;

@Sharable
public class ClientHandlerAdapter extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(ClientHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (MemberConfig.getInstance().isAutoSpread()) {
            InfoCommand infoCommand = new InfoCommand();
            infoCommand.setClusters(AllMember.getInstance().getClusters());
            ctx.channel().writeAndFlush(infoCommand);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
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
