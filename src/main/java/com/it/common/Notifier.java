package com.it.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import java.util.List;

public class Notifier {
  private static final Logger logger = LoggerFactory.getLogger(Notifier.class);
  private Multimap<String, Latch> latchMap;

  public Notifier() {
    latchMap = TreeMultimap.create();
  }

  public Multimap<String, Latch> getLatchMap() {
    return latchMap;
  }

  public void createLatches(String key, List<Integer> thresholds, int upCount, int downCount) {
    for (Integer threshold : thresholds) {
      createLatch(key, threshold, upCount, downCount);
    }
  }

  public void createLatch(String key, int threshold, int upCount, int downCount) {
    Latch latch = new Latch(threshold, upCount, downCount);
    latchMap.put(key, latch);
  }

  public void setValue(String key, int value) {
    for (Latch latch : latchMap.get(key)) {
      latch.setValue(value);
    }
  }

  public void send() {
    StringBuilder content = new StringBuilder();
    for (String key : latchMap.keySet()) {
      for (Latch latch : latchMap.get(key)) {
        if (latch.isStatusChanged()) {
          content.append(latch.getThreshold());
          content.append(latch.getStatus().toString());
          // MailSender.getInstance().send("tiny657@naver.com", "testTitle", content.toString());
          logger.info(content.toString());
          break;
        }
      }
    }
  }
}
