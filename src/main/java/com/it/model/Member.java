package com.it.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class Member implements Comparable<Member>, Serializable {
    private static final long serialVersionUID = -3329506897816445518L;

    private String host;
    private int port;
    private Status status = Status.SHUTDOWN;
    private boolean me = false;

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

    public boolean isMe() {
        return me;
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
        if (status == Status.RUNNING) {
            return true;
        }
        return false;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        } else {
            status = this.status.toString();
        }

        return host + ":" + port + "(" + status + ")";
    }
}