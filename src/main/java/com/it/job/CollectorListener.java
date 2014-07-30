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

package com.it.job;

import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.EvictingQueue;
import com.it.common.Consts;
import com.it.common.Notifier;
import com.it.config.MemberConfig;
import com.it.domain.FilesystemMetrics.FileSystem;
import com.it.domain.NetworkMetrics.Network;
import com.it.domain.ResourceMetrics;

public class CollectorListener implements JobListener {
  private final Logger logger = LoggerFactory.getLogger(CollectorListener.class);
  private static CollectorListener instance = new CollectorListener();

  private Network prevNetwork = null;
  private List<FileSystem> prevFileSystems = null;

  private ResourceMetrics resource = null;
  private EvictingQueue<ResourceMetrics> resourceHistory = EvictingQueue.create(30 * 24 * 60 * 60);

  private Notifier notifier;

  private CollectorListener() {
    notifier = new Notifier();
    notifier.createLatches(Consts.CPU, MemberConfig.getInstance().getThresholdCpus(), 1, 5);
    notifier.createLatches(Consts.LOADAVERAGE, MemberConfig.getInstance()
        .getThresholdLoadAverages(), 1, 5);
    notifier.createLatches(Consts.MEMORY, MemberConfig.getInstance().getThresholdMemories(), 1, 5);
  }

  public static CollectorListener getInstance() {
    return instance;
  }

  public EvictingQueue<ResourceMetrics> getHistory() {
    return resourceHistory;
  }

  public ResourceMetrics getLastResourceMetrics() {
    return resource;
  }

  @Override
  public String getName() {
    return "CollectorListener";
  }

  @Override
  public void jobToBeExecuted(JobExecutionContext context) {
    context.getJobDetail().getJobDataMap().put(Consts.NETWORK, prevNetwork);
    context.getJobDetail().getJobDataMap().put(Consts.FILESYSTEMS, prevFileSystems);
  }

  @Override
  public void jobExecutionVetoed(JobExecutionContext context) {}

  @Override
  public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
    JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    prevNetwork = (Network) dataMap.get(Consts.NETWORK);
    prevFileSystems = (List<FileSystem>) dataMap.get(Consts.FILESYSTEMS);
    resource = (ResourceMetrics) dataMap.get(Consts.RESOURCE);
    resourceHistory.add(resource);

    notifier.setValue(Consts.CPU,
        resource.getCpuUserUsedPercent() + resource.getCpuSysUsedPercent());
    notifier.setValue(Consts.LOADAVERAGE, resource.getLoadAvg1M());
    notifier.setValue(Consts.MEMORY, resource.getMemUsedMB() * 100 / resource.getMemTotalMB());
    notifier.send();
  }
}
