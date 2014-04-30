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
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            // Start the client.
            // ChannelFuture f = b.connect(host, port).sync();
            ChannelFuture f;
            while (true) {
                f = b.connect(host, port).await();
                if (f.isSuccess()) {
                    break;
                }
            }

            logger.info("connected {}:{}", host, port);

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
            logger.info("client closed {}:{}", host, port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}