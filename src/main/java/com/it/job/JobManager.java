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

import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;

import com.it.common.Consts;
import org.hyperic.sigar.Sigar;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.KeyMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.config.MemberConfig;

public class JobManager {
  private final Logger log = LoggerFactory.getLogger(JobManager.class);

  private static JobManager instance = new JobManager();
  private Scheduler scheduler;

  private JobManager() {}

  public static JobManager getInstance() {
    return instance;
  }

  public void runCollectorJob() {
    if (MemberConfig.getInstance().isMonitorEnable()) {
      try {
        JobKey jobKey = new JobKey("collector", "group");
        JobDetail job = newJob(CollectorJob.class).withIdentity(jobKey).build();
        job.getJobDataMap().put(Consts.SIGAR, new Sigar());

        Trigger trigger =
            newTrigger().withIdentity("collectorTrigger", "group").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();

        scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.getListenerManager().addJobListener(CollectorListener.getInstance(),
            KeyMatcher.keyEquals(jobKey));

        scheduler.scheduleJob(job, trigger);
        scheduler.start();
      } catch (SchedulerException e) {
        e.printStackTrace();
      }
    }
  }

  public void shutdownJob() {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }
}
