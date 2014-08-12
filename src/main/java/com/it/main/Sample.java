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

package com.it.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.common.DataSender;
import com.it.common.HandlerType;
import com.it.config.JoptConfig;

public class Sample {
  private static final Logger logger = LoggerFactory.getLogger(Sample.class);

  public static void main(String[] args) throws InterruptedException {
    final HandlerType type = HandlerType.OBJECT;
    Stima stima;
    switch (type) {
      case OBJECT:
        stima = createStimaWithObjectHandler(args);
        stima.start();

        if (JoptConfig.getInstance().isSender()) {
          for (int i = 0; i < 10; i++) {
            DataSender.sendAnycast("b", new TestCommand());
          }
          stima.shutdown();
        }
        break;

      case STRING:
        stima = createStimaWithStringHandler(args);
        stima.start();

        if (JoptConfig.getInstance().isSender()) {
          for (int i = 0; i < 10; i++) {
            DataSender.sendAnycast("b", "test" + i + "\n");
          }
          stima.shutdown();
        }
        break;
    }
  }

  private static Stima createStimaWithObjectHandler(String[] args) {
    Stima.Builder builder = Stima.builder();
    builder.handlerType(HandlerType.OBJECT);
    builder.serverHandler(new ServerHandler());
    builder.clientHandler(new ClientHandler());
    builder.args(args);

    return builder.build();
  }

  private static Stima createStimaWithStringHandler(String[] args) {
    Stima.Builder builder = Stima.builder();
    builder.handlerType(HandlerType.STRING);
    builder.serverHandler(new ServerHandler());
    builder.clientHandler(new ClientHandler());
    builder.args(args);

    return builder.build();
  }
}
