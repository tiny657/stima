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

package com.it.config;

import java.io.FileNotFoundException;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpecBuilder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JoptConfig {
  private static final Logger logger = LoggerFactory.getLogger(JoptConfig.class);
  private static JoptConfig instance = new JoptConfig();

  private static final String DEFAULT_MEMBER_PROPERTIES = "member.properties";
  private static final String DEFAULT_MAIL_PROPERTIES = "mail.properties";

  private OptionParser parser = new OptionParser();
  private ArgumentAcceptingOptionSpec<String> memberPropertiesOpt = parser
      .accepts("memberProp", "member properties file").withOptionalArg().ofType(String.class)
      .defaultsTo(DEFAULT_MEMBER_PROPERTIES);
  private ArgumentAcceptingOptionSpec<String> mailPropertiesOpt = parser
      .accepts("mailProp", "mail properties file").withOptionalArg().ofType(String.class)
      .defaultsTo(DEFAULT_MAIL_PROPERTIES);
  private ArgumentAcceptingOptionSpec<String> clusterOpt = parser
      .accepts("cluster", "this server's cluster").withOptionalArg().ofType(String.class)
      .defaultsTo(StringUtils.EMPTY);
  private ArgumentAcceptingOptionSpec<Integer> idOpt = parser.accepts("id", "this server's id")
      .withOptionalArg().ofType(Integer.class).defaultsTo(-1);
  private ArgumentAcceptingOptionSpec<Integer> monitorPortOpt = parser
      .accepts("monitorPort", "this monitor's port").withOptionalArg().ofType(Integer.class)
      .defaultsTo(-1);
  private OptionSpecBuilder senderOpt = parser.accepts("sender", "If set, this is the sender.");

  private String cluster;
  private int id;
  private int monitorPort;
  private boolean isSender;

  private JoptConfig() {}

  public static JoptConfig getInstance() {
    return instance;
  }

  public void init(String[] args) throws FileNotFoundException {
    OptionSet options = parser.parse(args);
    MemberConfig.getInstance().setPropertiesFile(options.valueOf(memberPropertiesOpt));
    MailConfig.getInstance().setPropertiesFile(options.valueOf(mailPropertiesOpt));
    setCluster(options.valueOf(clusterOpt));
    setId(options.valueOf(idOpt));
    setMonitorPort(options.valueOf(monitorPortOpt));
    setSender(options.has(senderOpt));
  }

  public String getCluster() {
    return cluster;
  }

  public void setCluster(String cluster) {
    this.cluster = cluster;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getMonitorPort() {
    return monitorPort;
  }

  public void setMonitorPort(int monitorPort) {
    this.monitorPort = monitorPort;
  }

  public boolean isSender() {
    return isSender;
  }

  public void setSender(boolean isSender) {
    this.isSender = isSender;
  }
}
