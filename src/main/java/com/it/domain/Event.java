package com.it.domain;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Event {
  private static final Logger logger = LoggerFactory.getLogger(Event.class);

  private DateTime eventTime;
  private String subject, content;

  public Event() {}

  public Event(String subject, String content) {
    eventTime = DateTime.now();
    this.subject = subject;
    this.content = content;
  }

  public String getEventTime() {
    return eventTime.toString();
  }

  public String getSubject() {
    return subject;
  }

  public String getContent() {
    return content;
  }
}
