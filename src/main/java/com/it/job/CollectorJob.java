package com.it.job;

import java.util.List;

import org.hyperic.sigar.Sigar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.it.job.FilesystemMetrics.FileSystem;
import com.it.job.NetworkMetrics.Network;

public class CollectorJob implements Job {
  private final Logger logger = LoggerFactory.getLogger(CollectorJob.class);

  private Sigar sigar;
  private Network prevNetwork;
  private List<FileSystem> prevFileSystems;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    // receive data
    JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    sigar = (Sigar) dataMap.get("sigar");
    prevNetwork = (Network) dataMap.get("network");
    prevFileSystems = (List<FileSystem>) dataMap.get("fileSystems");

    // cpu
    CpuMetrics cpuMetrics = new CpuMetrics(sigar);

    // memory
    MemoryMetrics memoryMetrics = new MemoryMetrics(sigar);

    // file system
    FilesystemMetrics fileSystemMetrics = new FilesystemMetrics(sigar);
    List<FileSystem> fileSystems = fileSystemMetrics.fileSystems();
    List<FileSystem> fileSystemsDiff = Lists.newArrayList();
    if (prevFileSystems != null) {
      for (int i = 0; i < fileSystems.size(); i++) {
        FileSystem fileSystemDiff = fileSystems.get(i).diff(prevFileSystems.get(i));
        fileSystemsDiff.add(fileSystemDiff);
      }
    }

    // network
    NetworkMetrics networkMetrics = new NetworkMetrics(sigar);
    Network network = networkMetrics.sumOfNetworks();
    Network networkDiff = null;
    if (prevNetwork != null) {
      networkDiff = network.diff(prevNetwork);
    }

    // send data
    context.getJobDetail().getJobDataMap().put("network", network);
    context.getJobDetail().getJobDataMap().put("fileSystems", fileSystems);

    if (prevFileSystems != null && prevNetwork != null) {
      ResourceMetrics resource =
          new ResourceMetrics(cpuMetrics, memoryMetrics, fileSystemsDiff, networkDiff);
      context.getJobDetail().getJobDataMap().put("resource", resource);
    }
  }
}
