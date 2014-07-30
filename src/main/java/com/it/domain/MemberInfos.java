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
