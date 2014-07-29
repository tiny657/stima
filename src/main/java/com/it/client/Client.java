package com.it.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.domain.AllMember;
import com.it.domain.Member;
import com.it.domain.Status;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;

abstract public class Client extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  protected Member myInfo;
  protected ClientHandlerAdapter clientHandlerAdapter;
  protected ChannelFuture channelFuture = null;

  protected boolean stopped = false;
  protected boolean isStartup = false;

  protected Client(Member member) {
    myInfo = member;
  }

  public void setClientHandler(ClientHandlerAdapter clientHandlerAdapter) {
    this.clientHandlerAdapter = clientHandlerAdapter;
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
