package com.it.test;


public class TestServer {
    public static void main(String[] args) throws Exception {
        It it = new It(args);
        Thread.sleep(2000);
        it.sendAnycast("global", "I'm " + it.getProfile());
        Thread.sleep(2000);
        it.sendAnycast("global", "I'm " + it.getProfile());
        Thread.sleep(2000);
        it.sendAnycast("global", "I'm " + it.getProfile());
    }
}