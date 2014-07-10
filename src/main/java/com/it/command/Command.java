package com.it.command;

import java.io.Serializable;

public class Command implements Serializable {
  private static final long serialVersionUID = 4258334791271723894L;

  private String myCluster;
  private int myId;

  public Command() {}

  public Command(String myCluster, int myId) {
    this.myCluster = myCluster;
    this.myId = myId;
  }

  public String getMyCluster() {
    return myCluster;
  }

  public int getMyId() {
    return myId;
  }

  @Override
  public String toString() {
    return myCluster + "." + myId;
  }
}
