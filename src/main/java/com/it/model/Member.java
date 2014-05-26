package com.it.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Member implements Comparable<Member>, Serializable {
    private static final long serialVersionUID = -6870770836608106922L;

    private String host;
    private int port;
    private boolean isRunning = false;
    transient private boolean me = false;

    public Member() {
    }

    public Member(String host, String port, String myHost, int myPort) {
        this(host, Integer.valueOf(port), myHost, myPort);
    }

    public Member(String host, String port) {
        this(host, Integer.valueOf(port));
    }

    public Member(String host, int port, String myHost, int myPort) {
        this(host, port);
        if (host.equals(myHost) && port == myPort) {
            me = true;
        }
    }

    public Member(String host, int port) {
        this.host = host;
        this.port = port;
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

    public String getHostPort() {
        return host + ":" + port;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public boolean equals(Member member) {
        return equals(member.host, member.port);
    }

    public boolean equals(String host, int port) {
        if (StringUtils.equals(this.host, host) && this.port == port) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(Member member) {
        if (host.compareTo(member.getHost()) == 0) {
            return port - member.getPort();
        } else {
            return host.compareTo(member.getHost());
        }
    }

    @Override
    public String toString() {
        String status;
        if (me) {
            status = "me";
        } else if (isRunning) {
            status = "running";
        } else {
            status = "standby";
        }

        return host + ":" + port + "(" + status + ")";
    }
}