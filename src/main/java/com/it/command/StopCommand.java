package com.it.command;

public class StopCommand extends Command {
    private static final long serialVersionUID = -1887486853983762813L;

    public StopCommand() {
    }

    public StopCommand(String host, int port) {
        super(host, port);
    }
}