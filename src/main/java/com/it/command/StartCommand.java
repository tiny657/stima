package com.it.command;

public class StartCommand extends Command {
  private static final long serialVersionUID = 761930442713735729L;

  public StartCommand() {}

  public StartCommand(String host, int port) {
    super(host, port);
  }

  @Override
  public String toString() {
    return "StartCommand";
  }
}
