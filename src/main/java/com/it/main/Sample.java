package com.it.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.Sender;
import com.it.config.JoptConfig;
import com.it.job.JobManager;

public class Sample {
    private static final Logger logger = LoggerFactory.getLogger(Sample.class);

    public static void main(String[] args) throws InterruptedException {
        JobManager.getInstance().runCollectorJob();
        ItRunner.getInstance().execute(new ServerHandler(),
                new ClientHandler(), args);
        if (JoptConfig.getInstance().isSender()) {
            for (int i = 0; i < 3; i++) {
                Sender.sendAnycast("b", new TestCommand());
                Thread.sleep(1000);
            }
            ItRunner.getInstance().shutdown();
        }
    }
}