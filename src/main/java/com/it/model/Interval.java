package com.it.model;

public class Interval {
  private String host;
  private int port;
  private String desc;
  private int sentTps;
  private long totalSent;
  private short masterPriority, priorityPoint;
  private String status;

  public Interval() {}

  public Interval(String host, int port, String desc, int sentTps, long totalSent,
      short masterPriority, short priorityPoint, String status) {
    this.host = host;
    this.port = port;
    this.desc = desc;
    this.sentTps = sentTps;
    this.totalSent = totalSent;
    this.masterPriority = masterPriority;
    this.priorityPoint = priorityPoint;
    this.status = status;
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

  public void setPort(int port) {
    this.port = port;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public int getSentTps() {
    return sentTps;
  }

  public void setSentTps(int sentTps) {
    this.sentTps = sentTps;
  }

  public long getTotalSent() {
    return totalSent;
  }

  public void setTotalSent(long totalSent) {
    this.totalSent = totalSent;
  }

  public short getMasterPriority() {
    return masterPriority;
  }

  public void setMasterPriority(short masterPriority) {
    this.masterPriority = masterPriority;
  }

  public short getPriorityPoint() {
    return priorityPoint;
  }

  public void setPriorityPoint(short priorityPoint) {
    this.priorityPoint = priorityPoint;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
