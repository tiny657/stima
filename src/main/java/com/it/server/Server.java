package com.it.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.model.AllMember;
import com.it.model.Member;
import com.it.model.Status;

public class Server extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  private Member myInfo;
  private ServerHandlerAdapter serverHandlerAdapter;
  private boolean isStartup = false;

  public Server(Member member) {
    myInfo = member;
  }

  public void setServerHandler(ServerHandlerAdapter serverHandlerAdapter) {
    this.serverHandlerAdapter = serverHandlerAdapter;
  }

  public Member getMyInfo() {
    return myInfo;
  }

  public String getHost() {
    return myInfo.getHost();
  }

  public int getPort() {
    return myInfo.getPort();
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
              socketChannel.pipeline().addLast(new ObjectEncoder(),
                  new ObjectDecoder(ClassResolvers.cacheDisabled(null)), serverHandlerAdapter);
            }
          }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

      ChannelFuture channelFuture = bootstrap.bind(myInfo.getPort()).sync();

      AllMember.getInstance().getMemberInfos().put(myInfo, channelFuture);
      myInfo.setStatus(Status.STANDBY);
      isStartup = true;
      logger.info("server started ({})", myInfo.toString());

      awaitDisconnection(channelFuture);
    } catch (InterruptedException e) {
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  public void await() {
    while (!isStartup) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
    }
  }

  private void awaitDisconnection(ChannelFuture channelFuture) throws InterruptedException {
    channelFuture.channel().closeFuture().sync();
    logger.info("server closed ({})", myInfo.getHostPort());
  }
}
