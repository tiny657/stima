package com.it.model;

import io.netty.channel.ChannelFuture;

import java.util.Map;

import com.google.common.collect.Maps;
import com.it.client.ItClient;

public class ServerInfo {
    private Map<Server, ChannelFuture> channelFutureMap;
    private Map<Server, ItClient> itClientMap;

    public ServerInfo() {
        channelFutureMap = Maps.newHashMap();
        itClientMap = Maps.newHashMap();
    }

    public ChannelFuture getChannelFuture(Server server) {
        return channelFutureMap.get(server);
    }

    public void add(Server server, ChannelFuture channelFuture) {
        channelFutureMap.put(server, channelFuture);
    }

    public ItClient getItClient(Server server) {
        return itClientMap.get(server);
    }

    public void add(Server server, ItClient itClient) {
        itClientMap.put(server, itClient);
    }

    public void remove(Server server) {
        channelFutureMap.remove(server);
        itClientMap.remove(server);
    }
}