package com.it.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.Sender;
import com.it.config.MemberConfig;

public class Sample {
    private static final Logger logger = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) throws InterruptedException {
        ItRunner.getInstance().execute(new ServerHandler(),
                new ClientHandler(), args);
        if (MemberConfig.getInstance().isSender()) {
            for (int i = 0; i < 3000; i++) {
                Sender.sendAnycast("b", new TestCommand());
                Thread.sleep(100);
            }
            ItRunner.getInstance().shutdown();
        }
    }
}