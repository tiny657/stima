package com.it.domain;

import java.util.List;

import com.google.common.collect.Lists;

public class History {
  public static History instance = new History();
  public static List<Event> events = Lists.newArrayList();

  public static History getInstance() {
    return instance;
  }

  public List<Event> getAlerts() {
    return events;
  }

  public void save(String subject, String content) {
    events.add(new Event(subject, content));
  }
}
