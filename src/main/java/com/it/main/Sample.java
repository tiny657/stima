package com.it.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.command.StopCommand;
import com.it.common.Config;
import com.it.common.Sender;

public class Sample {
    private static final Logger logger = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) throws InterruptedException {
        ItRunner.getInstance().execute(new ServerHandler(),
                new ClientHandler(), args);
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            Sender.sendAnycast("a", new TestCommand());
        }

        Sender.sendBroadcast(new StopCommand(Config.getInstance().getHost(),
                Config.getInstance().getPort()));
        Thread.sleep(1000);
        ItRunner.getInstance().shutdownNow();
    }
}