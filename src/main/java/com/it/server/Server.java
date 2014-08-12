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

package com.it.server;

import java.util.concurrent.CountDownLatch;

import com.it.common.HandlerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.domain.AllMember;
import com.it.domain.Member;
import com.it.domain.Status;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

abstract public class Server extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  protected Member myInfo;
  protected CountDownLatch startupLatch = new CountDownLatch(1);
  protected HandlerType handlerType;
  protected ChannelHandler handler;

  protected Server(Member member) {
    myInfo = member;
  }

  public void setHandlerType(HandlerType handlerType) {
    this.handlerType = handlerType;
  }

  public void setHandler(ChannelHandler channelHandler) {
    handler = channelHandler;
  }

  public Member getMyInfo() {
    return myInfo;
  }

  public String getHost() {
    return myInfo.getHost();
  }

  abstract public int getPort();

  public void awaitConnection() {
    try {
      startupLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  protected void awaitDisconnection(ChannelFuture channelFuture) throws InterruptedException {
    channelFuture.channel().closeFuture().sync();

    // change status to Shutdown
    AllMember.getInstance().getMemberByDataPort(myInfo.getHost(), myInfo.getDataPort())
        .setStatus(Status.SHUTDOWN);

    logger.info("server closed ({}:{})", getHost(), getPort());
  }
}
