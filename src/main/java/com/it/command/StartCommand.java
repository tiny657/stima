package com.it.command;

public class StartCommand extends Command {
  private static final long serialVersionUID = -297920249892130471L;

  public StartCommand(String myCluster, int myId) {
    super(myCluster, myId);
  }

  @Override
  public String toString() {
    return "StartCommand";
  }
}
