package com.it.command;

public class StopCommand extends Command {
  private static final long serialVersionUID = -1887486853983762813L;

  public StopCommand() {}

  public StopCommand(String srcHost, int srcPort) {
    super(srcHost, srcPort);
  }

  @Override
  public String toString() {
    return "StopCommand";
  }
}
