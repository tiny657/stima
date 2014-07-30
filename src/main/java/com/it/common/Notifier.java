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

package com.it.common;

import com.it.config.MemberConfig;
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
    StringBuilder subject = new StringBuilder();
    StringBuilder content = new StringBuilder();
    for (String key : latchMap.keySet()) {
      for (Latch latch : latchMap.get(key)) {
        if (latch.isStatusChanged()) {
          subject.append(key).append(" ").append(latch.getThreshold()).append(" ")
              .append(latch.getStatus().toString());
          MailSender.getInstance().send(MemberConfig.getInstance().getMonitorMail(),
              subject.toString(), content.toString());
          logger.info(content.toString());
          break;
        }
      }
    }
  }
}
