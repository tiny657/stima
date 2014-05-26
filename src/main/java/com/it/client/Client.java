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
    private static final Logger logger = LoggerFactory
            .getLogger(Client.class);

    private String profile;
    private Member member;

    public Client(Member member) {
        this(member.getHost(), member.getPort());
    }

    public Client(String host, int port) {
        this.profile = "me" + port;
        member = new Member(host, port);
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
                    socketChannel.pipeline().addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers
                                    .cacheDisabled(null)), new ClientHandler());
                }
            });

            while (true) {
                ChannelFuture channelFuture = awaitConnection(bootstrap);

                // update member and memberInfo.
                Member connectedMember = AllMember.getInstance().getMember(
                        member);
                AllMember.getInstance().getMemberInfos()
                        .put(connectedMember, channelFuture, this);

                logger.info("Connection({}) is established.",
                        member.getHostPort());
                logger.info(AllMember.getInstance().toString());

                awaitDisconnection(channelFuture);
            }
        } catch (InterruptedException e) {
            logger.info("Connection({}) is closed.", member.getHostPort());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private ChannelFuture awaitConnection(Bootstrap bootstrap)
            throws InterruptedException {
        logger.info("Connecting to {}", member.getHostPort());

        ChannelFuture channelFuture;
        do {
            channelFuture = bootstrap.connect(member.getHost(),
                    member.getPort()).await();
            Thread.sleep(1000);
        } while (!channelFuture.isSuccess());

        // From standby to running.
        AllMember.getInstance().setStatus(member, true);

        return channelFuture;
    }

    private void awaitDisconnection(ChannelFuture channelFuture)
            throws InterruptedException {
        channelFuture.channel().closeFuture().sync();

        // From running to standby.
        AllMember.getInstance().setStatus(member, false);

        logger.info("Connection({}) is closed.", member.getHostPort());
        logger.info(AllMember.getInstance().toString());
    }
}