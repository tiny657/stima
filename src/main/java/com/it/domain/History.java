package com.it.domain;

import java.util.List;

import com.google.common.collect.Lists;

public class History {
  public static History instance = new History();
  public static List<Alert> alerts = Lists.newArrayList();

  public static History getInstance() {
    return instance;
  }

  public List<Alert> getAlerts() {
    return alerts;
  }

  public void save(String subject, String content) {
    alerts.add(new Alert(subject, content));
  }
}
