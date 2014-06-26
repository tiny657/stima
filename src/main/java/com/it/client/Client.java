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
import com.it.model.Status;

public class Client extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  private Member myInfo;
  private ClientHandlerAdapter clientHandlerAdapter;
  private ChannelFuture channelFuture = null;

  private boolean stopped = false;
  private boolean isStartup = false;

  public Client(Member member) {
    myInfo = member;
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
        public void initChannel(SocketChannel socketChannel) throws Exception {
          socketChannel.pipeline().addLast(new ObjectEncoder(),
              new ObjectDecoder(ClassResolvers.cacheDisabled(null)), clientHandlerAdapter);
        }
      });

      AllMember.getInstance().getMemberInfos().put(myInfo, this);
      while (!stopped) {
        channelFuture = connect(bootstrap);
        update(channelFuture);
        awaitDisconnection(channelFuture);
        AllMember.getInstance().me().calculatePriorityPointWhenDisconnect(myInfo);
      }
    } catch (InterruptedException e) {
      logger.info("Connection({}) is closed.", myInfo.getHostPort());
    } finally {
      workerGroup.shutdownGracefully();
    }
  }

  public void await() {
    while (!isStartup) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void shutdown() {
    if (channelFuture != null) {
      channelFuture.channel().close();
    }
    stopped = true;
    logger.info("The client({}) is stopped.", myInfo.getHostPort());
  }

  private ChannelFuture connect(Bootstrap bootstrap) throws InterruptedException {
    logger.info("Connecting to {}", myInfo.getHostPort());

    ChannelFuture channelFuture;
    do {
      channelFuture = bootstrap.connect(myInfo.getHost(), myInfo.getPort()).await();
      isStartup = true;
      Thread.sleep(100);
    } while (!stopped && !channelFuture.isSuccess());

    if (channelFuture.isSuccess()) {
      logger.info("Connection({}) is established.", myInfo.getHostPort());
    }

    return channelFuture;
  }

  private void update(ChannelFuture channelFuture) {
    AllMember.getInstance().getMemberInfos().put(myInfo, channelFuture);

    // update status
    if (myInfo.getStatus() == Status.SHUTDOWN) {
      myInfo.setStatus(Status.STANDBY);
    }

    logger.info(AllMember.getInstance().toString());
  }

  private void awaitDisconnection(ChannelFuture channelFuture) throws InterruptedException {
    channelFuture.channel().closeFuture().sync();
    channelFuture = null;

    myInfo.setStatus(Status.SHUTDOWN);

    logger.info("Connection({}) is closed.", myInfo.getHostPort());
    logger.info(AllMember.getInstance().toString());
  }
}
