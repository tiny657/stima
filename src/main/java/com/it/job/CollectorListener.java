package com.it.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.job.NetworkMetrics.Network;

public class CollectorListener implements JobListener {
    private final Logger logger = LoggerFactory
            .getLogger(CollectorListener.class);

    private Network prevNetwork = null;

    @Override
    public String getName() {
        return "CollectorListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        context.getJobDetail().getJobDataMap().put("network", prevNetwork);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context,
            JobExecutionException jobException) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        prevNetwork = (Network) dataMap.get("network");
    }
}