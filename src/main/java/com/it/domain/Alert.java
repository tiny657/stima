package com.it.domain;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alert {
  private static final Logger logger = LoggerFactory.getLogger(Alert.class);

  private DateTime eventTime;
  private String subject, content;

  public Alert() {}

  public Alert(String subject, String content) {
    eventTime = DateTime.now();
    this.subject = subject;
    this.content = content;
  }
}
