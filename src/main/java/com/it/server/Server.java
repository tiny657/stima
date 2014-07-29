package com.it.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.it.domain.AllMember;
import com.it.domain.Member;
import com.it.domain.Status;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;

abstract public class Server extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Server.class);
  protected Member myInfo;
  protected ServerHandlerAdapter serverHandlerAdapter;
  protected boolean isStartup = false;
  protected List<ChannelHandler> pipelines = Lists.newArrayList();

  protected Server(Member member) {
    myInfo = member;
  }

  public void addPipeline(ChannelHandler channelHandler) {
    pipelines.add(channelHandler);
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

  abstract public int getPort();

  public void await() {
    while (!isStartup) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
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
