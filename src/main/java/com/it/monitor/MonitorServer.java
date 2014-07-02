package com.it.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class MonitorServer extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(MonitorServer.class);

  private final int port;

  public MonitorServer(int port) {
    this.port = port;
  }

  @Override
  public void run() {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) throws Exception {
              socketChannel.pipeline().addLast(new HttpRequestDecoder(), new HttpResponseEncoder(),
                  new MonitorServerHandler());
            }
          });

      ChannelFuture channelFuture = bootstrap.bind(port).sync();
      logger.info("monitor server started (port: {})", port);
      awaitDisconnection(channelFuture);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

  private void awaitDisconnection(ChannelFuture channelFuture) throws InterruptedException {
    channelFuture.channel().closeFuture().sync();
    logger.info("monitor server closed (port: {})", port);
  }
}
