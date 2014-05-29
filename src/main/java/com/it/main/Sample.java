package com.it.main;


public class Sample {
    public static void main(String[] args) throws InterruptedException {
        ItRunner.getInstance().execute(new ServerHandler(),
                new ClientHandler(), args);
//        for (int i = 0; i < 3; i++) {
//            Thread.sleep(3000);
//            Sender.sendAnycast("a", new InfoCommand());
//        }
    }
}