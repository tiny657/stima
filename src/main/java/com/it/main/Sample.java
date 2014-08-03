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
import com.it.config.JoptConfig;

import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Sample {
  private static final Logger logger = LoggerFactory.getLogger(Sample.class);

  public static void main(String[] args) throws InterruptedException {
    Stima stima = createStima(args);
    stima.start();
    if (JoptConfig.getInstance().isSender()) {
      for (int i = 0; i < 10; i++) {
        DataSender.sendAnycast("b", new TestCommand());
      }
      stima.shutdown();
    }
  }

  private static Stima createStima(String[] args) {
    Stima.Builder builder = Stima.builder();

    // server handler
    builder.serverHandler(new ObjectEncoder());
    builder.serverHandler(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
    builder.serverHandler(new ServerHandler());

    // client handler
    builder.clientHandler(new ObjectEncoder());
    builder.clientHandler(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
    builder.serverHandler(new ClientHandler());

    // args
    builder.args(args);

    return builder.build();
  }
}
