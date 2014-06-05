package com.it.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.Config;
import com.it.common.Sender;

public class Sample {
    private static final Logger logger = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) throws InterruptedException {
        ItRunner.getInstance().execute(new ServerHandler(),
                new ClientHandler(), args);
        if (Config.getInstance().isSender()) {
            for (int i = 0; i < 3000; i++) {
                Sender.sendAnycast("b", new TestCommand());
            }
            ItRunner.getInstance().shutdown();
        }
    }
}