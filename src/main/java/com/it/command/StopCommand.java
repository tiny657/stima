package com.it.command;

public class StopCommand extends Command {
  private static final long serialVersionUID = 8700197457995030892L;

  public StopCommand(String myCluster, int myId) {
    super(myCluster, myId);
  }

  @Override
  public String toString() {
    return "StopCommand";
  }
}
