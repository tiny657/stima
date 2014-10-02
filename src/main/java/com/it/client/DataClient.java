/*
 * licensed to the apache software foundation (asf) under one or more contributor license
 * agreements. see the notice file distributed with this work for additional information regarding
 * copyright ownership. the asf licenses this file to you under the apache license, version 2.0 (the
 * "license"); you may not use this file except in compliance with the license. you may obtain a
 * copy of the license at
 * 
 * http://www.apache.org/licenses/license-2.0
 * 
 * unless required by applicable law or agreed to in writing, software distributed under the license
 * is distributed on an "as is" basis, without warranties or conditions of any kind, either express
 * or implied. see the license for the specific language governing permissions and limitations under
 * the license.
 */

package com.it.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.domain.AllMember;
import com.it.domain.Member;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

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
          switch (handlerType){
            case STRING:
              socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
              socketChannel.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
              socketChannel.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
              socketChannel.pipeline().addLast(handler);
              break;

            case OBJECT:
              socketChannel.pipeline().addLast(new ObjectEncoder());
              socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
              socketChannel.pipeline().addLast(handler);
              break;
          }
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
