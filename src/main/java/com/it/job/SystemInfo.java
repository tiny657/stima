package com.it.job;

import java.io.Serializable;

import javax.management.openmbean.CompositeData;

public class SystemInfo extends MonitorInfo implements Serializable {
    private static final long serialVersionUID = 6182343315980418666L;

    public enum System {
        LINUX, WINDOW
    }

    private System system;
    private BandWidth bandWidth;
    private long totalCpuValue;
    private long idleCpuValue;
    private long freeMemory;
    private long totalMemory;
    private float cpuUsedPercentage;
    private String ip;
    private String customValues;

    @Override
    public void parse(CompositeData cd) {
        if (cd == null) {
            return;
        }
        try {
            long collectTime = getLong(cd, "collectTime");
            setCollectTime(collectTime);
            String string = getString(cd, "system");
            this.system = System.valueOf(string);
            this.totalCpuValue = getLong(cd, "totalCpuValue");
            this.idleCpuValue = getLong(cd, "idlecpu");
            this.freeMemory = getLong(cd, "freeMemory");
            this.totalMemory = getLong(cd, "totalMemory");
            this.cpuUsedPercentage = getFloat(cd, "CPUUsedPercentage");

            if (containsKey(cd, "bandWidth")) {
                CompositeData bandWidth = (CompositeData) getObject(cd,
                        "bandWidth");
                this.bandWidth = new BandWidth(collectTime);
                long recivedPerSec = getLong(bandWidth, "recivedPerSec");
                long sentPerSec = getLong(bandWidth, "sentPerSec");
                this.bandWidth.setRecivedPerSec(recivedPerSec);
                this.bandWidth.setSentPerSec(sentPerSec);
            }
            if (containsKey(cd, "customValues")) {
                this.setCustomValues(getString(cd, "customValues"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

    public void setSystem(String system) {
        this.system = System.valueOf(system);
    }

    public void setCPUUsedPercentage(float cpuUsedPercentage) {
        this.cpuUsedPercentage = cpuUsedPercentage;
    }

    public float getCPUUsedPercentage() {
        return cpuUsedPercentage;
    }

    public long getIdlecpu() {
        return idleCpuValue;
    }

    public long getTotalCpuValue() {
        return totalCpuValue;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setTotalCpuValue(long totalCpuValue) {
        this.totalCpuValue = totalCpuValue;
    }

    public void setIdleCpuValue(long idleCpuValue) {
        this.idleCpuValue = idleCpuValue;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public BandWidth getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(BandWidth bandWidth) {
        this.bandWidth = bandWidth;
    }

    public String getCustomValues() {
        return customValues;
    }

    public void setCustomValues(String customValues) {
        this.customValues = customValues;
    }

    @Override
    public String toString() {
        return "sent: " + bandWidth.getSentPerSec() + ", received: "
                + bandWidth.getRecivedPerSec() + ", cpu: " + cpuUsedPercentage
                + ", Memory: " + freeMemory + "/" + totalMemory;
    }
}
