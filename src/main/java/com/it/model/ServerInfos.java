package com.it.model;

import io.netty.channel.ChannelFuture;

import java.util.Map;

import com.google.common.collect.Maps;
import com.it.client.ItClient;

public class ServerInfos {
    private Map<Server, ChannelFuture> channelFutureMap;
    private Map<Server, ItClient> itClientMap;

    public ServerInfos() {
        channelFutureMap = Maps.newHashMap();
        itClientMap = Maps.newHashMap();
    }

    public void put(Server server, ChannelFuture channelFuture,
            ItClient itClient) {
        if (server == null) {
            return;
        }

        put(server, channelFuture);
        put(server, itClient);
    }

    public ChannelFuture getChannelFuture(Server server) {
        return channelFutureMap.get(server);
    }

    public void put(Server server, ChannelFuture channelFuture) {
        channelFutureMap.put(server, channelFuture);
    }

    public ItClient getItClient(Server server) {
        return itClientMap.get(server);
    }

    public void put(Server server, ItClient itClient) {
        itClientMap.put(server, itClient);
    }

    public void removeInfo(Server server) {
        channelFutureMap.remove(server);
        itClientMap.remove(server);
    }
}