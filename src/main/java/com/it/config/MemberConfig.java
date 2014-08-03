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

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.it.common.Utils;
import com.it.domain.AllMember;
import com.it.domain.Member;
import com.it.exception.InvalidMemberException;

public class MemberConfig {
  private static final Logger logger = LoggerFactory.getLogger(MemberConfig.class);
  private static MemberConfig instance = new MemberConfig();

  private static final String CLUSTER = "cluster";
  private static final String MY_CLUSTER = "my.cluster";
  private static final String MY_ID = "my.id";
  private static final String MY_DESC = "my.desc";
  private static final String SPREAD_TIME = "config.spreadTime";
  private static final String MASTER_PRIORITY = "master.priority";
  private static final String MONITOR_ENABLE = "monitor.enable";
  private static final String MONITOR_MAIL = "monitor.mail";
  private static final String MONITOR_PORT = "monitor.port";
  private static final String MONITOR_THRESHOLD_CPU = "monitor.threshold.cpu";
  private static final String MONITOR_THRESHOLD_LOADAVERAGE = "monitor.threshold.loadAverage";
  private static final String MONITOR_THRESHOLD_MEMORY = "monitor.threshold.memory";

  private PropertiesConfiguration config;
  private String propertiesFile;

  private String myCluster;
  private int myId;
  private String myDesc;

  private int spreadTime = 5;

  private boolean monitorEnable;
  private int monitorPort;
  private String monitorMail;

  private List<Integer> thresholdCpus;
  private List<Integer> thresholdLoadAverages;
  private List<Integer> thresholdMemories;

  private MemberConfig() {}

  public static MemberConfig getInstance() {
    return instance;
  }

  public void setPropertiesFile(String propertiesFile) {
    this.propertiesFile = propertiesFile;
  }

  public void init() throws ConfigurationException {
    config = new PropertiesConfiguration(propertiesFile);
    config.setAutoSave(true);

    setMyCluster(config.getString(MY_CLUSTER));
    setMyId(config.getInt(MY_ID));
    setMyDesc(config.getString(MY_DESC));

    // config
    setSpreadTime(config.getInt(SPREAD_TIME));

    // monitor
    setMonitorEnable(config.getBoolean(MONITOR_ENABLE));
    setMonitorPort(config.getInt(MONITOR_PORT));
    setMonitorMail(Joiner.on(",").join(config.getList(MONITOR_MAIL)));
    setThresholdCpus(config.getList(MONITOR_THRESHOLD_CPU));
    setThresholdLoadAverage(config.getList(MONITOR_THRESHOLD_LOADAVERAGE));
    setThresholdMemories(config.getList(MONITOR_THRESHOLD_MEMORY));

    // from JOPT
    setMyCluster(JoptConfig.getInstance().getCluster());
    setMyId(JoptConfig.getInstance().getId());
    setMonitorPort(JoptConfig.getInstance().getMonitorPort());

    // add cluster
    AllMember.getInstance().addClusters(getClustersArray());

    // add member
    for (String cluster : AllMember.getInstance().getClusters().getClusterNames()) {
      for (String idHostPort : getMembers(cluster)) {
        if (!Utils.isMemberValid(idHostPort)) {
          throw new InvalidMemberException(idHostPort + " is invalid.");
        }
        String[] split = StringUtils.split(idHostPort, ":");
        boolean me = StringUtils.equals(cluster, myCluster) && (Utils.parseInt(split[0]) == myId);
        AllMember.getInstance().addMember(cluster,
            new Member(split[0], split[1], split[2], split[3], me));
      }
    }

    logger.info(AllMember.getInstance().getClusters().toString());

    // set master.priority & bootup time & desc.
    Member me = AllMember.getInstance().me();
    me.setMasterPriority(config.getShort(MASTER_PRIORITY, (short) 0));
    me.setBootupTime(DateTime.now());
    me.setDesc(myDesc);
  }

  public void addMember(String cluster, Member member) {
    if (!ArrayUtils.contains(getClustersArray(), cluster)) {
      config.addProperty(CLUSTER, cluster);
    }

    config.addProperty(getSubCluster(cluster), member.getHostPort());
  }

  public void removeCluster(String removedCluster) {
    List<String> clusters = getClustersList();

    for (int i = 0; i < clusters.size(); i++) {
      if (StringUtils.equals(clusters.get(i), removedCluster)) {
        clusters.remove(i);
      }
    }

    config.setProperty(CLUSTER, Joiner.on(",").join(clusters));
  }

  public void removeMember(String cluster, Member member) {
    List<String> hostPorts = getMembers(cluster);
    config.clearProperty(getSubCluster(cluster));
    String removedHostPort = member.getHostPort();
    for (String hostPort : hostPorts) {
      if (StringUtils.equals(removedHostPort, hostPort)) {
        hostPorts.remove(hostPort);
        break;
      }
    }

    for (String hostPort : hostPorts) {
      config.addProperty(getSubCluster(cluster), hostPort);
    }
  }

  public String getMyCluster() {
    return myCluster;
  }

  public void setMyCluster(String myCluster) {
    if (StringUtils.equals(myCluster, StringUtils.EMPTY)) {
      return;
    }
    this.myCluster = myCluster;
  }

  public int getMyId() {
    return myId;
  }

  public void setMyId(int myId) {
    if (myId == -1) {
      return;
    }
    this.myId = myId;
  }

  public String getMyDesc() {
    return myDesc;
  }

  public void setMyDesc(String myDesc) {
    this.myDesc = myDesc;
  }

  public int getSpreadTime() {
    return spreadTime;
  }

  public void setSpreadTime(int spreadTime) {
    this.spreadTime = spreadTime;
  }

  public boolean isAutoSpread() {
    return getSpreadTime() != 0;
  }

  public boolean isMonitorEnable() {
    return monitorEnable;
  }

  public void setMonitorEnable(boolean monitorEnable) {
    this.monitorEnable = monitorEnable;
  }

  public int getMonitorPort() {
    return monitorPort;
  }

  public void setMonitorPort(int monitorPort) {
    if (monitorPort == -1) {
      return;
    }
    this.monitorPort = monitorPort;
  }

  public String getMonitorMail() {
    return monitorMail;
  }

  public void setMonitorMail(String monitorMail) {
    this.monitorMail = monitorMail;
  }

  public List<Integer> getThresholdCpus() {
    return thresholdCpus;
  }

  public void setThresholdCpus(List<String> thresholdCpus) {
    this.thresholdCpus = getIntegerList(thresholdCpus);
  }

  public List<Integer> getThresholdLoadAverages() {
    return thresholdLoadAverages;
  }

  public void setThresholdLoadAverage(List<String> thresholdLoadAverages) {
    this.thresholdLoadAverages = getIntegerList(thresholdLoadAverages);
  }

  public List<Integer> getThresholdMemories() {
    return thresholdMemories;
  }

  public void setThresholdMemories(List<String> thresholdMemories) {
    this.thresholdMemories = getIntegerList(thresholdMemories);
  }

  @SuppressWarnings("unchecked")
  public List<String> getMembers(String cluster) {
    return config.getList(getSubCluster(cluster));
  }

  private String[] getClustersArray() {
    return config.getStringArray(CLUSTER);
  }

  @SuppressWarnings("unchecked")
  private List<String> getClustersList() {
    return config.getList(CLUSTER);
  }

  private String getSubCluster(String cluster) {
    return CLUSTER + "." + cluster;
  }

  private List<Integer> getIntegerList(List<String> thresholds) {
    List<Integer> result = Lists.newArrayList();
    for (String threshold : thresholds) {
      result.add(Utils.parseInt(threshold));
    }
    return result;
  }
}
