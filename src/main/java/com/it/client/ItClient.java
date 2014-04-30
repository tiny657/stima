package com.it.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.AllServer;

public class ItClient implements Runnable {
    private static final Logger logger = LoggerFactory
            .getLogger(ItClient.class);

    private String host;
    private int port;

    public ItClient(String host, int port) {
        this.host = host;
        this.port = port;
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
                    socketChannel.pipeline().addLast(new ClientHandler());
                }
            });

            while (true) {
                logger.info("connecting {}:{}", host, port);
                
                ChannelFuture channelFuture = awaitConnection(bootstrap);
                AllServer.getInstance().setStatus(host, port, true);

                logger.info("connected {}:{}", host, port);
                logger.info(AllServer.getInstance().toString());

                // wait until closed.
                channelFuture.channel().closeFuture().sync();
                AllServer.getInstance().setStatus(host, port, false);

                logger.info("closed {}:{}", host, port);
                logger.info(AllServer.getInstance().toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public ChannelFuture awaitConnection(Bootstrap bootstrap)
            throws InterruptedException {
        ChannelFuture channelFuture;
        do {
            channelFuture = bootstrap.connect(host, port).await();
        } while (!channelFuture.isSuccess());

        return channelFuture;
    }
}