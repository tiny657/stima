/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

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
