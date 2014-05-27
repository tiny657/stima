package com.it.main;

import com.it.common.Sender;

public class Sample {
    public static void main(String[] args) throws InterruptedException {
        ItRunner.execute(new ServerHandler(), new ClientHandler(), args);
        for (int i = 0; i < 3; i++) {
            Thread.sleep(3000);
            Sender.sendAnycast("a", new Command(i, "hi"));
        }
    }
}