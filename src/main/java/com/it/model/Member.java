package com.it.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.Utils;

public class Member implements Comparable<Member>, Serializable {
    private static final long serialVersionUID = 4145607009200321641L;
    private static final Logger logger = LoggerFactory.getLogger(Member.class);

    private Date bootupTime;
    private int masterPriority, priorityPoint;
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
        this(host, Utils.parseInt(port), myHost, myPort);
    }

    public Member(String host, String port) {
        this(host, Utils.parseInt(port));
    }

    public Member(String host, int port, String myHost, int myPort) {
        this(host, port);
        if (host.equals(myHost) && port == myPort) {
            me = true;
        }
    }

    public Member(String host, int port) {
        this.host = host;
        if (Utils.isPortValid(port)) {
            this.port = port;
        } else {
            logger.error("port({}) is invalid.", port);
        }
    }

    public boolean isMe() {
        return me;
    }

    public boolean isEarlier(Member member) {
        return bootupTime.compareTo(member.getBootupTime()) < 0;
    }

    public Date getBootupTime() {
        return bootupTime;
    }

    public void setBootupTime(Date bootupTime) {
        this.bootupTime = bootupTime;
    }

    public int getMasterPriority() {
        return masterPriority;
    }

    public void setMasterPriority(int masterPriority) {
        this.masterPriority = masterPriority;
    }

    public boolean isMaster() {
        return getPriorityPoint() == 0;
    }

    public boolean isSlave() {
        return !isMaster();
    }

    public int getPriorityPoint() {
        return priorityPoint;
    }

    public void increasePriorityPoint() {
        priorityPoint++;
    }

    public void decreasePriorityPoint() {
        priorityPoint--;
    }

    public void calculatePriorityPointWhenConnect(Member member) {
        if (masterPriority > member.getMasterPriority()) {
            increasePriorityPoint();
        } else if (masterPriority < member.getMasterPriority()) {
            member.increasePriorityPoint();
        } else {
            if (isEarlier(member)) {
                member.increasePriorityPoint();
            } else {
                increasePriorityPoint();
            }
        }
        logger.info("master: {}", isMaster());
    }

    public void calculatePriorityPointWhenDisconnect(Member member) {
        if (masterPriority > member.getMasterPriority()) {
            decreasePriorityPoint();
        } else if (masterPriority < member.getMasterPriority()) {
            member.decreasePriorityPoint();
        } else {
            if (isEarlier(member)) {
                member.decreasePriorityPoint();
            } else {
                decreasePriorityPoint();
            }
        }
        logger.info("master: {}", isMaster());
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
            logger.info("sent: {}/{}", sentTPS, totalSent);
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
            logger.info("received: {}/{}", receivedTPS, totalReceived);
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