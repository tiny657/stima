package com.it.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.command.Command;
import com.it.command.InfoCommand;
import com.it.command.StartCommand;
import com.it.command.StopCommand;
import com.it.common.Config;
import com.it.model.AllMember;

public class ClientHandlerAdapter extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(ClientHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (Config.getInstance().isAutoSpread()) {
            InfoCommand infoCommand = new InfoCommand();
            infoCommand.setClusters(AllMember.getInstance().getClusters());
            ctx.channel().writeAndFlush(infoCommand);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Command) {
            if (msg instanceof StartCommand) {
                StartCommand cmd = (StartCommand) msg;
                logger.info("data received: StartCommand");
                ReferenceCountUtil.release(msg);
            } else if (msg instanceof StopCommand) {
                StartCommand cmd = (StartCommand) msg;
                logger.info("data received: StopCommand");
                ReferenceCountUtil.release(msg);
            }
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
