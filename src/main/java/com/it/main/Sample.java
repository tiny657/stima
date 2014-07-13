package com.it.main;

import com.it.common.MsgSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.config.JoptConfig;

public class Sample {
  private static final Logger logger = LoggerFactory.getLogger(Sample.class);

  public static void main(String[] args) throws InterruptedException {
    ItRunner.getInstance().execute(new ServerHandler(), new ClientHandler(), args);
    if (JoptConfig.getInstance().isSender()) {
      for (int i = 0; i < 30; i++) {
        MsgSender.sendAnycast("b", new TestCommand());
        Thread.sleep(100);
      }
      ItRunner.getInstance().shutdown();
    }
  }
}
