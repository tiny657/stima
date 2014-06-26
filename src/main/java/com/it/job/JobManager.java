package com.it.job;

import static org.quartz.JobBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.TriggerBuilder.*;

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
        job.getJobDataMap().put("sigar", new Sigar());

        Trigger trigger =
            newTrigger().withIdentity("collectorTrigger", "group").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();

        scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.getListenerManager().addJobListener(CollectorListener.getInstance(),
            KeyMatcher.keyEquals(jobKey));

        scheduler.scheduleJob(job, trigger);
        scheduler.start();
      } catch (SchedulerException e) {
        log.error("SchedulerException: ", e);
      }
    }
  }

  public void shutdownJob() {
    try {
      scheduler.shutdown();
    } catch (SchedulerException e) {
      log.error("SchedulerException: ", e);
    }
  }
}
