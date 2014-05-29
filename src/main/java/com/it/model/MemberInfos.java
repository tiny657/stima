package com.it.model;

import io.netty.channel.ChannelFuture;

import java.util.Map;

import com.google.common.collect.Maps;
import com.it.client.Client;

public class MemberInfos {
    private Map<String, ChannelFuture> channelFutureMap;
    private Map<String, Client> clientMap;

    public MemberInfos() {
        channelFutureMap = Maps.newHashMap();
        clientMap = Maps.newHashMap();
    }

    public void put(Member member, ChannelFuture channelFuture, Client client) {
        if (member == null) {
            return;
        }

        put(member, channelFuture);
        put(member, client);
    }

    public ChannelFuture getChannelFuture(Member member) {
        return channelFutureMap.get(member);
    }

    public void put(Member member, ChannelFuture channelFuture) {
        channelFutureMap.put(member.getHostPort(), channelFuture);
    }

    public Client getClient(Member member) {
        return clientMap.get(member.getHostPort());
    }

    public void put(Member member, Client client) {
        clientMap.put(member.getHostPort(), client);
    }

    public void removeInfo(Member member) {
        channelFutureMap.remove(member);
        clientMap.remove(member);
    }
}