package com.it.client;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.domain.AllMember;
import com.it.domain.Member;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ControlClient extends Client {
  private static final Logger logger = LoggerFactory.getLogger(ControlClient.class);

  public ControlClient(Member member) {
    super(member);
  }

  @Override
  public int getPort() {
    return myInfo.getControlPort();
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
              new ObjectDecoder(ClassResolvers.cacheDisabled(null)), new ClientHandlerAdapter());
        }
      });

      AllMember.getInstance().getMemberInfos().putControlClient(myInfo, this);
      while (!stopped) {
        channelFuture = connect(bootstrap);
        update(channelFuture);
        updateStatus();
        awaitDisconnection(channelFuture);
        AllMember.getInstance().me().calculatePriorityPointWhenDisconnect(myInfo);
      }
    } catch (InterruptedException e) {
      logger.info("Connection({}:{}) is closed.", getHost(), getPort());
    } finally {
      workerGroup.shutdownGracefully();
    }
  }

  private void update(ChannelFuture channelFuture) {
    AllMember.getInstance().getMemberInfos().putControlChannelFuture(myInfo, channelFuture);
  }
}
