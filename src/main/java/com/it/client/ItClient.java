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

import com.it.model.AllServer;
import com.it.model.Server;
import com.it.sender.UnicastSender;

public class ItClient implements Runnable {
    private static final Logger logger = LoggerFactory
            .getLogger(ItClient.class);

    private String profile;

    private String serverHost;
    private int serverPort;

    public ItClient(String profile, String serverHost, int serverPort) {
        this.profile = profile;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
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
                logger.info("connecting {}:{}", serverHost, serverPort);

                ChannelFuture channelFuture = awaitConnection(bootstrap);
                AllServer.getInstance().setStatus(serverHost, serverPort, true);
                Server server = AllServer.getInstance().getServer(serverHost,
                        serverPort);
                if (server != null) {
                    server.setChannelFuture(channelFuture);
                }

                logger.info("connected {}:{}", serverHost, serverPort);
                logger.info(AllServer.getInstance().toString());

                UnicastSender.send(serverHost, serverPort, "Hi, I'm " + profile
                        + " to " + serverHost + ":" + serverPort);

                // wait until closed.
                channelFuture.channel().closeFuture().sync();
                AllServer.getInstance()
                        .setStatus(serverHost, serverPort, false);

                logger.info("closed {}:{}", serverHost, serverPort);
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
            channelFuture = bootstrap.connect(serverHost, serverPort).await();
        } while (!channelFuture.isSuccess());

        return channelFuture;
    }
}