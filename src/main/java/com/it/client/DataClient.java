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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.it.domain.AllMember;
import com.it.domain.Member;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

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
          List<ChannelHandler> copiedHandlers = Lists.newArrayList(handlers);
          socketChannel.pipeline().addLast(
              copiedHandlers.toArray(new ChannelHandler[handlers.size()]));
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
