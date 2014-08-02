/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.it.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.it.domain.AllMember;
import com.it.domain.Member;
import com.it.domain.Status;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

abstract public class Client extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  protected Member myInfo;
  protected List<ChannelHandler> handlers = Lists.newArrayList();
  protected ChannelFuture channelFuture = null;

  protected boolean stopped = false;
  protected boolean isStartup = false;

  protected Client(Member member) {
    myInfo = member;
  }

  public void addHandlers(List<ChannelHandler> channelHandlers) {
    handlers.addAll(channelHandlers);
  }

  public String getHost() {
    return myInfo.getHost();
  }

  abstract public int getPort();

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
    logger.info("The client({}:{}) is stopped.", getHost(), getPort());
  }

  protected ChannelFuture connect(Bootstrap bootstrap) throws InterruptedException {
    logger.info("Connecting to {}:{}", getHost(), getPort());

    ChannelFuture channelFuture;
    do {
      channelFuture = bootstrap.connect(getHost(), getPort()).await();
      isStartup = true;
      Thread.sleep(100);
    } while (!stopped && !channelFuture.isSuccess());

    if (channelFuture.isSuccess()) {
      logger.info("Connection({}:{}) is established.", getHost(), getPort());
    }

    return channelFuture;
  }

  protected void updateStatus() {
    // update status
    if (myInfo.getStatus() == Status.SHUTDOWN) {
      myInfo.setStatus(Status.STANDBY);
    }

    logger.info(AllMember.getInstance().toString());
  }

  protected void awaitDisconnection(ChannelFuture channelFuture) throws InterruptedException {
    channelFuture.channel().closeFuture().sync();

    myInfo.setStatus(Status.SHUTDOWN);

    logger.info("Connection({}:{}) is closed.", getHost(), getPort());
    logger.info(AllMember.getInstance().toString());
  }
}
