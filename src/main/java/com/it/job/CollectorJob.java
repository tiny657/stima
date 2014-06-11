package com.it.job;

import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectorJob implements Job {
    private final Logger logger = LoggerFactory.getLogger(CollectorJob.class);

    private Sigar sigar;
    private SystemInfo prev;

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        sigar = (Sigar) dataMap.get("sigar");
        prev = (SystemInfo) dataMap.get("systemInfo");
        if (prev == null) {
            prev = new SystemInfo();
            try {
                prev.setBandWidth(getNetworkUsage());
            } catch (SigarException e) {
                e.printStackTrace();
            }
        }

        SystemInfo systemInfo = new SystemInfo();
        systemInfo.setCollectTime(System.currentTimeMillis());
        try {
            BandWidth networkUsage = getNetworkUsage();
            BandWidth bandWidth = networkUsage.adjust(prev.getBandWidth());
            systemInfo.setBandWidth(bandWidth);
            systemInfo.setCPUUsedPercentage((float) sigar.getCpuPerc()
                    .getCombined() * 100);
            Cpu cpu = sigar.getCpu();
            systemInfo.setTotalCpuValue(cpu.getTotal());
            systemInfo.setIdleCpuValue(cpu.getIdle());
            Mem mem = sigar.getMem();
            systemInfo.setTotalMemory(mem.getTotal() / 1024L / 1024L);
            systemInfo.setFreeMemory(mem.getActualFree() / 1024L / 1024L);
            systemInfo
                    .setSystem(OperatingSystem.IS_WIN32 ? SystemInfo.System.WINDOW
                            : SystemInfo.System.LINUX);
        } catch (Throwable e) {
            logger.error("Error while getting system perf data:{}",
                    e.getMessage());
            logger.debug("Error trace is ", e);
        }
        context.getJobDetail().getJobDataMap().put("systemInfo", systemInfo);
        logger.info(systemInfo.toString());
    }

    public BandWidth getNetworkUsage() throws SigarException {
        BandWidth bandWidth = new BandWidth(System.currentTimeMillis());
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
