package com.it.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.domain.AllMember;
import com.it.domain.Member;
import com.it.domain.Status;

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

public class DataClient extends Client {
  private static final Logger logger = LoggerFactory.getLogger(DataClient.class);

  public DataClient(Member member) {
    super(member);
  }

  @Override
  public int getPort() {
    return myInfo.getDataPort();
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

      AllMember.getInstance().getMemberInfos().putDataClient(myInfo, this);
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
    AllMember.getInstance().getMemberInfos().putDataChannelFuture(myInfo, channelFuture);
  }
}
