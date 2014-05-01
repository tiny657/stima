package com.it.model;

import io.netty.channel.ChannelFuture;

import org.apache.commons.lang3.StringUtils;

public class Server {
    private ChannelFuture channelFuture;
    private String host;
    private int port;
    private boolean isRunning = false;

    public Server(String host, String port) {
        this(host, Integer.valueOf(port));
    }

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = Integer.valueOf(port);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean equals(String host, int port) {
        if (StringUtils.equals(this.host, host) && this.port == port) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return host + ":" + port + "("
                + (isRunning == true ? "running" : "standby") + ")";
    }
}