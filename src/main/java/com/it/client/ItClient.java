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

import com.it.common.Sender;
import com.it.model.AllServer;
import com.it.model.Server;

public class ItClient extends Thread {
    private static final Logger logger = LoggerFactory
            .getLogger(ItClient.class);

    private String profile;
    private Server server;

    public ItClient(Server server) {
        this(server.getHost(), server.getPort());
    }

    public ItClient(String host, int port) {
        this.profile = "me" + port;
        server = new Server(host, port);
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
                ChannelFuture channelFuture = awaitConnection(bootstrap);

                // update server and serverInfo.
                Server connectedServer = AllServer.getInstance().getServer(
                        server);
                AllServer.getInstance().getServerInfos()
                        .put(connectedServer, channelFuture, this);

                logger.info("Connection({}) is established.",
                        server.getHostPort());
                logger.info(AllServer.getInstance().toString());
                
                awaitDisconnection(channelFuture);
            }
        } catch (InterruptedException e) {
            logger.info("Connection({}) is closed.", server.getHostPort());
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private ChannelFuture awaitConnection(Bootstrap bootstrap)
            throws InterruptedException {
        logger.info("Connecting to {}", server.getHostPort());

        ChannelFuture channelFuture;
        do {
            channelFuture = bootstrap.connect(server.getHost(),
                    server.getPort()).await();
            Thread.sleep(1000);
        } while (!channelFuture.isSuccess());

        // From standby to running.
        AllServer.getInstance().setStatus(server, true);

        return channelFuture;
    }

    private void awaitDisconnection(ChannelFuture channelFuture)
            throws InterruptedException {
        channelFuture.channel().closeFuture().sync();

        // From running to standby.
        AllServer.getInstance().setStatus(server, false);

        logger.info("Connection({}) is closed.", server.getHostPort());
        logger.info(AllServer.getInstance().toString());
    }
}