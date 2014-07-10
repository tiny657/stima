package com.it.model;

import java.io.Serializable;
import java.util.Date;

import com.it.exception.InvalidMemberException;
import com.it.job.CollectorListener;
import com.it.job.ResourceMetrics;
import com.mchange.v2.util.ResourceClosedException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.it.common.Utils;

public class Member implements Comparable<Member>, Serializable {
  private static final long serialVersionUID = -5112411599686358922L;

  private static final Logger logger = LoggerFactory.getLogger(Member.class);

  private Date bootupTime;
  private short masterPriority, priorityPoint;
  private int id;
  private String host;
  private int port;
  private String desc;
  private Status status = Status.SHUTDOWN;
  private boolean me = false;

  transient private long totalSent, totalReceived;
  transient private long prevTotalSent, prevTotalReceived;
  transient private int timeForReceived, timeForSent;
  transient private int sentTPS, receivedTPS;

  public Member() {}

  public Member(String id, String host, String port, boolean me) {
    this(Utils.parseInt(id), host, Utils.parseInt(port));
    setMe(me);
  }

  public Member(int id, String host, int port) {
    setId(id);
    setHost(host);
    setPort(port);
  }

  public boolean isMe() {
    return me;
  }

  public void setMe(boolean me) {
    this.me = me;
  }

  public ResourceMetrics getResource() {
    if (me) {
      return CollectorListener.getInstance().getLastResourceMetrics();
    } else {
      return null;
    }
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

  @JsonIgnore
  public short getMasterPriority() {
    return masterPriority;
  }

  public void setMasterPriority(short masterPriority) {
    this.masterPriority = masterPriority;
  }

  public boolean isMaster() {
    if (getMasterPriority() == 0) {
      return false;
    }

    return getPriorityPoint() == 0;
  }

  @JsonIgnore
  public boolean isStandby() {
    return !isMaster();
  }

  @JsonIgnore
  public short getPriorityPoint() {
    return priorityPoint;
  }

  public void increasePriorityPoint() {
    priorityPoint++;
  }

  public void decreasePriorityPoint() {
    priorityPoint--;
  }

  public void calculatePriorityPointWhenConnect(Member member) {
    if (getMasterPriority() == 0 || member.getMasterPriority() == 0) {
      return;
    }

    if (masterPriority > member.getMasterPriority()) {
      increasePriorityPoint();
    } else if (masterPriority == member.getMasterPriority() && !isEarlier(member)) {
      increasePriorityPoint();
    }
    logger.info("master: {}", isMaster());
  }

  public void calculatePriorityPointWhenDisconnect(Member member) {
    if (getMasterPriority() == 0 || member.getMasterPriority() == 0) {
      return;
    }

    if (masterPriority > member.getMasterPriority()) {
      decreasePriorityPoint();
    } else if (masterPriority == member.getMasterPriority() && !isEarlier(member)) {
      decreasePriorityPoint();
    }
    logger.info("master: {}", isMaster());
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @JsonIgnore
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @JsonIgnore
  public int getPort() {
    return port;
  }

  public void setPort(String port) {
    setPort(Utils.parseInt(port));
  }

  public void setPort(int port) {
    if (!Utils.isPortValid(port)) {
      throw new InvalidMemberException("Port (" + port + ") is invalid.");
    }

    this.port = port;
  }

  public String getHostPort() {
    return host + ":" + port;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  @JsonIgnore
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
    if (timeForSent != DateTime.now().getSecondOfDay()) {
      sentTPS = 0;
      timeForSent = DateTime.now().getSecondOfDay();
    }
    return sentTPS;
  }

  public long increaseSentCount() {
    totalSent++;
    if (timeForSent != DateTime.now().getSecondOfDay()) {
      sentTPS = (int) (totalSent - prevTotalSent);
      prevTotalSent = totalSent;
      logger.info("sentTps: {}, totalSent: {}", sentTPS, totalSent);
      timeForSent = DateTime.now().getSecondOfDay();
    }

    return totalSent;
  }

  public long getTotalReceived() {
    return totalReceived;
  }

  public int getReceivedTPS() {
    if (timeForReceived != DateTime.now().getSecondOfDay()) {
      receivedTPS = 0;
      timeForReceived = DateTime.now().getSecondOfDay();
    }
    return receivedTPS;
  }

  public long increaseReceivedCount() {
    totalReceived++;
    if (timeForReceived != DateTime.now().getSecondOfDay()) {
      receivedTPS = (int) (totalReceived - prevTotalReceived);
      prevTotalReceived = totalReceived;
      logger.info("receivedTps: {}, totalReceived: {}", receivedTPS, totalReceived);
      timeForReceived = DateTime.now().getSecondOfDay();
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

    return id + ":" + host + ":" + port + "(" + status + ", sent: " + +sentTPS + "/" + totalSent
        + ", received: " + receivedTPS + "/" + totalReceived + ")";
  }
}
