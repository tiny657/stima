package com.it.model;

import io.netty.channel.ChannelFuture;

import java.util.Map;

import com.google.common.collect.Maps;

public class ChannelFutureInfo {
    private Map<Server, ChannelFuture> channelFutureMap;

    public ChannelFutureInfo() {
        channelFutureMap = Maps.newHashMap();
    }

    public ChannelFuture getChannelFuture(Server server) {
        return channelFutureMap.get(server);
    }

    public void add(Server server, ChannelFuture channelFuture) {
        channelFutureMap.put(server, channelFuture);
    }
}