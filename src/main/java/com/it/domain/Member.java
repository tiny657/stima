/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.it.domain;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.it.common.Utils;
import com.it.exception.InvalidMemberException;
import com.it.job.CollectorListener;

public class Member implements Comparable<Member>, Serializable {
  private static final long serialVersionUID = 4848152770088013661L;

  private static final Logger logger = LoggerFactory.getLogger(Member.class);

  private DateTime bootupTime;
  private short masterPriority, priorityPoint;
  private int id;
  private String host;
  private int dataPort;
  private int controlPort;
  private String desc;
  private Status status = Status.SHUTDOWN;
  private boolean me = false;

  transient private long totalSent, totalReceived;
  transient private long prevTotalSent, prevTotalReceived;
  transient private int timeForReceived, timeForSent;
  transient private int sentTPS, receivedTPS;

  public Member() {}

  public Member(String id, String host, String dataPort, String controlPort, boolean me) {
    this(Utils.parseInt(id), host, Utils.parseInt(dataPort), Utils.parseInt(controlPort));
    setMe(me);
  }

  public Member(int id, String host, int dataPort, int controlPort) {
    setId(id);
    setHost(host);
    setDataPort(dataPort);
    setControlPort(controlPort);
  }

  public boolean isMe() {
    return me;
  }

  public void setMe(boolean me) {
    this.me = me;
  }

  public ResourceMetrics getResource() {
    if (!me) {
      return null;
    }

    return CollectorListener.getInstance().getLastResourceMetrics();
  }

  public boolean isBefore(Member member) {
    return bootupTime.isBefore(member.getBootupTime());
  }

  public boolean isAfter(Member member) {
    return !isBefore(member);
  }

  @JsonIgnore
  public DateTime getBootupTime() {
    return bootupTime;
  }

  public void setBootupTime(DateTime bootupTime) {
    this.bootupTime = bootupTime;
  }

  public String getStringBootupTime() {
    if (bootupTime == null) {
      return StringUtils.EMPTY;
    }

    return bootupTime.toString();
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
    } else if (masterPriority == member.getMasterPriority() && isAfter(member)) {
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
    } else if (masterPriority == member.getMasterPriority() && isAfter(member)) {
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
  public int getDataPort() {
    return dataPort;
  }

  public void setDataPort(String port) {
    setDataPort(Utils.parseInt(port));
  }

  public void setDataPort(int port) {
    if (!Utils.isPortValid(port)) {
      throw new InvalidMemberException("dataPort (" + port + ") is invalid.");
    }

    this.dataPort = port;
  }

  @JsonIgnore
  public int getControlPort() {
    return controlPort;
  }

  public void setControlPort(String port) {
    setControlPort(Utils.parseInt(port));
  }

  public void setControlPort(int port) {
    if (!Utils.isPortValid(port)) {
      throw new InvalidMemberException("controlPort (" + port + ") is invalid.");
    }

    this.controlPort = port;
  }

  public String getHostPort() {
    return host + ":" + dataPort + ":" + controlPort;
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
      logger.info("receivedTPS: {}, totalReceived: {}", receivedTPS, totalReceived);
      timeForReceived = DateTime.now().getSecondOfDay();
    }

    return totalReceived;
  }

  public boolean equals(Member member) {
    return equalsByDataPort(member.host, member.dataPort);
  }

  public boolean equalsByDataPort(String host, int dataPort) {
    if (StringUtils.equals(this.host, host) && this.dataPort == dataPort) {
      return true;
    }
    return false;
  }

  public boolean equalsByControlPort(String host, int controlPort) {
    if (StringUtils.equals(this.host, host) && this.controlPort == controlPort) {
      return true;
    }
    return false;
  }

  @Override
  public int compareTo(Member member) {
    if (host.compareTo(member.getHost()) == 0) {
      return dataPort - member.getDataPort();
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

    return id + ":" + host + ":" + dataPort + ":" + controlPort + "(" + status + ", sent: "
        + +sentTPS + "/" + totalSent + ", received: " + receivedTPS + "/" + totalReceived + ")";
  }
}
