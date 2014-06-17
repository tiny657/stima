package com.it.job;

import java.util.List;

import org.hyperic.sigar.Sigar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.job.FilesystemMetrics.FileSystem;
import com.it.job.NetworkMetrics.Network;

public class CollectorJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(CollectorJob.class);

    private Sigar sigar;
    private Network prevNetwork;
    private List<FileSystem> prevFileSystems;

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        sigar = (Sigar) dataMap.get("sigar");
        prevNetwork = (Network) dataMap.get("network");
        prevFileSystems = (List<FileSystem>) dataMap.get("fileSystems");

        FilesystemMetrics fileSystemMetrics = new FilesystemMetrics(sigar);
        List<FileSystem> fileSystems = fileSystemMetrics.fileSystems();
        if (prevFileSystems != null) {
            for (int i = 0; i < fileSystems.size(); i++) {
                logger.info("fileSystem: {}",
                        fileSystems.get(i).diff(prevFileSystems.get(i))
                                .toString());
            }
        }

        MemoryMetrics memoryMetrics = new MemoryMetrics(sigar);
        logger.info(memoryMetrics.mem().toString());
        logger.info(memoryMetrics.swap().toString());

        NetworkMetrics networkMetrics = new NetworkMetrics(sigar);
        Network network = networkMetrics.sumOfNetworks();

        if (prevNetwork != null) {
            logger.info(network.diff(prevNetwork).toString());
        }

        CpuMetrics cpuMetrics = new CpuMetrics(sigar);
        logger.info(cpuMetrics.cpu().toString());

        context.getJobDetail().getJobDataMap().put("network", network);
        context.getJobDetail().getJobDataMap().put("fileSystems", fileSystems);
    }
}