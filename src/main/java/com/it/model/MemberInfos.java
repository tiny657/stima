package com.it.model;

import io.netty.channel.ChannelFuture;

import java.util.Map;

import com.google.common.collect.Maps;
import com.it.client.ItClient;

public class MemberInfos {
    private Map<Member, ChannelFuture> channelFutureMap;
    private Map<Member, ItClient> itClientMap;

    public MemberInfos() {
        channelFutureMap = Maps.newHashMap();
        itClientMap = Maps.newHashMap();
    }

    public void put(Member member, ChannelFuture channelFuture,
            ItClient itClient) {
        if (member == null) {
            return;
        }

        put(member, channelFuture);
        put(member, itClient);
    }

    public ChannelFuture getChannelFuture(Member member) {
        return channelFutureMap.get(member);
    }

    public void put(Member member, ChannelFuture channelFuture) {
        channelFutureMap.put(member, channelFuture);
    }

    public ItClient getItClient(Member member) {
        return itClientMap.get(member);
    }

    public void put(Member member, ItClient itClient) {
        itClientMap.put(member, itClient);
    }

    public void removeInfo(Member member) {
        channelFutureMap.remove(member);
        itClientMap.remove(member);
    }
}