package com.it.main;

import com.it.command.StopCommand;
import com.it.common.Config;
import com.it.common.Sender;

public class Sample {
    public static void main(String[] args) throws InterruptedException {
        ItRunner.getInstance().execute(new ServerHandler(),
                new ClientHandler(), args);
        for (int i = 0; i < 3; i++) {
            Thread.sleep(3000);
            Sender.sendAnycast("a", new TestCommand("test"));
        }

        Sender.sendBroadcast(new StopCommand(Config.getInstance().getHost(),
                Config.getInstance().getPort()));
        ItRunner.getInstance().shutdownNow();
    }
}