package com.it.job;

import java.util.List;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
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

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        sigar = (Sigar) dataMap.get("sigar");
        prevNetwork = (Network) dataMap.get("network");

        FilesystemMetrics fileSystemMetrics = new FilesystemMetrics(sigar);
        List<FileSystem> filesystems = fileSystemMetrics.filesystems();
        logger.info("fileSystem: {}", filesystems.toString());

        MemoryMetrics memoryMetrics = new MemoryMetrics(sigar);
        logger.info(memoryMetrics.mem().toString());
        logger.info(memoryMetrics.swap().toString());

        NetworkMetrics networkMetrics = new NetworkMetrics(sigar);
        Network network = networkMetrics.sumOfNetworks();

        if (prevNetwork != null) {
            logger.info(network.diff(prevNetwork).toString());
        }

        SystemInfo systemInfo = new SystemInfo();
        try {
            systemInfo.setCPUUsedPercentage((float) sigar.getCpuPerc()
                    .getCombined() * 100);
            systemInfo.setLoadAverages(sigar.getLoadAverage());

            Cpu cpu = sigar.getCpu();
            systemInfo.setTotalCpuValue(cpu.getTotal());
            systemInfo.setIdleCpuValue(cpu.getIdle());
        } catch (Throwable e) {
            logger.error("Error while getting system perf data:{}",
                    e.getMessage());
            logger.debug("Error trace is ", e);
        }
        context.getJobDetail().getJobDataMap().put("network", network);

        logger.info(systemInfo.toString());
    }

    public BandWidth getNetworkUsage() throws SigarException {
        BandWidth bandWidth = new BandWidth();
        for (String each : sigar.getNetInterfaceList()) {
            try {
                NetInterfaceStat netInterfaceStat = sigar
                        .getNetInterfaceStat(each);
                bandWidth.setRecieved(bandWidth.getRecieved()
                        + netInterfaceStat.getRxBytes());
                bandWidth.setSent(bandWidth.getSent()
                        + netInterfaceStat.getTxBytes());
            } catch (Exception e) {
            }
        }
        return bandWidth;
    }
}