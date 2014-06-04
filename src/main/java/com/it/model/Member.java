package com.it.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Member implements Comparable<Member>, Serializable {
    private static final long serialVersionUID = 1176857242699928766L;
    private static final Logger logger = LoggerFactory.getLogger(Member.class);

    private String host;
    private int port;
    private Status status = Status.SHUTDOWN;
    private boolean me = false;

    transient private long totalSent, totalReceived;
    transient private int nowSecond;
    transient private int sentTPS, receivedTPS;

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

    public long getTotalSent() {
        return totalSent;
    }

    public int getSentTPS() {
        if (nowSecond != DateTime.now().getSecondOfDay()) {
            sentTPS = 0;
            nowSecond = DateTime.now().getSecondOfDay();
        }
        return sentTPS;
    }

    public long increaseSentCount() {
        sentTPS++;
        totalSent++;
        if (nowSecond != DateTime.now().getSecondOfDay()) {
            logger.info("sentTPS: {}", sentTPS);
            sentTPS = 0;
            nowSecond = DateTime.now().getSecondOfDay();
        }

        return totalSent;
    }

    public long getTotalReceived() {
        return totalReceived;
    }

    public int getReceivedTPS() {
        if (nowSecond != DateTime.now().getSecondOfDay()) {
            receivedTPS = 0;
            nowSecond = DateTime.now().getSecondOfDay();
        }
        return receivedTPS;
    }

    public long increaseReceivedCount() {
        receivedTPS++;
        totalReceived++;
        if (nowSecond != DateTime.now().getSecondOfDay()) {
            logger.info("receivedTPS: {}", receivedTPS);
            receivedTPS = 0;
            nowSecond = DateTime.now().getSecondOfDay();
        }

        return totalReceived;
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
        String status = this.status.toString();
        if (me) {
            status += ", me";
        }

        return host + ":" + port + "(" + status + ", sent: " + totalSent
                + ", received: " + totalReceived + ")";
    }
}