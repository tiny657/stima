package com.it.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.model.AllMember;
import com.it.model.Member;

public class Client extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private Member targetServer;
    private ClientHandlerAdapter clientHandlerAdapter;

    public Client(Member member) {
        targetServer = member;
    }

    public void setClientHandler(ClientHandlerAdapter clientHandlerAdapter) {
        this.clientHandlerAdapter = clientHandlerAdapter;
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel)
                        throws Exception {
                    socketChannel.pipeline()
                            .addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers
                                            .cacheDisabled(null)),
                                    clientHandlerAdapter);
                }
            });

            ChannelFuture channelFuture = connect(bootstrap);

            if (channelFuture.isSuccess()) {
                // update member and memberInfo.
                Member connectedMember = AllMember.getInstance().getMember(
                        targetServer);
                AllMember.getInstance().getMemberInfos()
                        .put(connectedMember, channelFuture, this);

                logger.info("Connection({}) is established.",
                        targetServer.getHostPort());
                logger.info(AllMember.getInstance().toString());
            }
            awaitDisconnection(channelFuture);
        } catch (InterruptedException e) {
            logger.info("Connection({}) is closed.", targetServer.getHostPort());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private ChannelFuture connect(Bootstrap bootstrap)
            throws InterruptedException {
        logger.info("Connecting to {}", targetServer.getHostPort());

        ChannelFuture channelFuture;
        channelFuture = bootstrap.connect(targetServer.getHost(),
                targetServer.getPort()).await();

        // From standby to running.
        AllMember.getInstance().setStatus(targetServer, true);

        return channelFuture;
    }

    private void awaitDisconnection(ChannelFuture channelFuture)
            throws InterruptedException {
        channelFuture.channel().closeFuture().sync();

        // From running to standby.
        AllMember.getInstance().setStatus(targetServer, false);

        logger.info("Connection({}) is closed.", targetServer.getHostPort());
        logger.info(AllMember.getInstance().toString());
    }
}
