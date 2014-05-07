package com.it.model;

import io.netty.channel.ChannelFuture;

import java.util.Map;

import com.google.common.collect.Maps;

public class ServerInfo {
    private Map<Server, ChannelFuture> channelFutureMap;
    private Map<Server, Runnable> clientRunnableMap;

    public ServerInfo() {
        channelFutureMap = Maps.newHashMap();
        clientRunnableMap = Maps.newHashMap();
    }

    public ChannelFuture getChannelFuture(Server server) {
        return channelFutureMap.get(server);
    }

    public void add(Server server, ChannelFuture channelFuture) {
        channelFutureMap.put(server, channelFuture);
    }

    public Runnable getClientThreadMap(Server server) {
        return clientRunnableMap.get(server);
    }

    public void add(Server server, Runnable clientThread) {
        clientRunnableMap.put(server, clientThread);
    }
}