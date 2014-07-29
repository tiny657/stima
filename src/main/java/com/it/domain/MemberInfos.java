package com.it.domain;

import io.netty.channel.ChannelFuture;

import java.util.Map;

import com.google.common.collect.Maps;
import com.it.client.Client;

public class MemberInfos {
  private Map<String, ChannelFuture> dataChannelFutureMap;
  private Map<String, ChannelFuture> controlChannelFutureMap;
  private Map<String, Client> dataClientMap;
  private Map<String, Client> controlClientMap;

  public MemberInfos() {
    dataChannelFutureMap = Maps.newHashMap();
    controlChannelFutureMap = Maps.newHashMap();
    dataClientMap = Maps.newHashMap();
    controlClientMap = Maps.newHashMap();
  }

  public ChannelFuture getDataChannelFuture(Member member) {
    return dataChannelFutureMap.get(member.getHostPort());
  }

  public void putDataChannelFuture(Member member, ChannelFuture channelFuture) {
    dataChannelFutureMap.put(member.getHostPort(), channelFuture);
  }

  public ChannelFuture getControlChannelFuture(Member member) {
    return controlChannelFutureMap.get(member.getHostPort());
  }

  public void putControlChannelFuture(Member member, ChannelFuture channelFuture) {
    controlChannelFutureMap.put(member.getHostPort(), channelFuture);
  }

  public Client getDataClient(Member member) {
    return dataClientMap.get(member.getHostPort());
  }

  public void putDataClient(Member member, Client client) {
    dataClientMap.put(member.getHostPort(), client);
  }

  public Client getControlClient(Member member) {
    return controlClientMap.get(member.getHostPort());
  }

  public void putControlClient(Member member, Client client) {
    controlClientMap.put(member.getHostPort(), client);
  }

  public void removeInfo(Member member) {
    dataChannelFutureMap.remove(member);
    controlChannelFutureMap.remove(member);
    dataClientMap.remove(member);
    controlClientMap.remove(member);
  }
}
