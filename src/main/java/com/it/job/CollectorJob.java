package com.it.job;

import java.util.List;

import com.it.common.Consts;
import com.it.domain.*;
import org.hyperic.sigar.Sigar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.it.domain.FilesystemMetrics.FileSystem;
import com.it.domain.NetworkMetrics.Network;

public class CollectorJob implements Job {
  private final Logger logger = LoggerFactory.getLogger(CollectorJob.class);

  private Sigar sigar;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    JobDataMap dataMap = context.getJobDetail().getJobDataMap();
    sigar = (Sigar) dataMap.get(Consts.SIGAR);
    Network prevNetwork = (Network) dataMap.get(Consts.NETWORK);
    List<FileSystem> prevFileSystems = (List<FileSystem>) dataMap.get(Consts.FILESYSTEMS);

    CpuMetrics cpuMetrics = getCpuMetrics();
    MemoryMetrics memoryMetrics = getMemoryMetrics();
    List<FileSystem> fileSystems = getFilesystems(context, prevFileSystems);
    Network network = getNetwork(context, prevNetwork);

    if (prevFileSystems != null && prevNetwork != null) {
      ResourceMetrics resource =
          new ResourceMetrics(cpuMetrics, memoryMetrics, fileSystems, network);
      context.getJobDetail().getJobDataMap().put(Consts.RESOURCE, resource);
    }
  }

  private CpuMetrics getCpuMetrics() {
    return new CpuMetrics(sigar);
  }

  private MemoryMetrics getMemoryMetrics() {
    return new MemoryMetrics(sigar);
  }

  private List<FileSystem> getFilesystems(JobExecutionContext context,
      List<FileSystem> prevFileSystems) {
    FilesystemMetrics fileSystemMetrics = new FilesystemMetrics(sigar);
    List<FileSystem> fileSystems = fileSystemMetrics.fileSystems();
    List<FileSystem> fileSystemsDiff = Lists.newArrayList();
    if (prevFileSystems != null) {
      for (int i = 0; i < fileSystems.size(); i++) {
        FileSystem fileSystemDiff = fileSystems.get(i).diff(prevFileSystems.get(i));
        fileSystemsDiff.add(fileSystemDiff);
      }
    }
    context.getJobDetail().getJobDataMap().put(Consts.FILESYSTEMS, fileSystems);
    return fileSystemsDiff;
  }

  private Network getNetwork(JobExecutionContext context, Network prevNetwork) {
    NetworkMetrics networkMetrics = new NetworkMetrics(sigar);
    Network network = networkMetrics.sumOfNetworks();
    Network networkDiff = null;
    if (prevNetwork != null) {
      networkDiff = network.diff(prevNetwork);
    }
    context.getJobDetail().getJobDataMap().put(Consts.NETWORK, network);
    return networkDiff;
  }
}
