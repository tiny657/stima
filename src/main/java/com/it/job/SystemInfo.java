package com.it.job;

import java.io.Serializable;

import javax.management.openmbean.CompositeData;

public class SystemInfo extends MonitorInfo implements Serializable {
    private static final long serialVersionUID = 6182343315980418666L;

    public enum System {
        LINUX, WINDOW
    }

    private System system;
    private long totalCpuValue;
    private long idleCpuValue;
    private long freeMemory;
    private long totalMemory;
    private float cpuUsedPercentage;
    private String ip;
    private double[] loadAverages = new double[3];

    @Override
    public void parse(CompositeData cd) {
        if (cd == null) {
            return;
        }
        try {
            String string = getString(cd, "system");
            this.system = System.valueOf(string);
            this.totalCpuValue = getLong(cd, "totalCpuValue");
            this.idleCpuValue = getLong(cd, "idlecpu");
            this.freeMemory = getLong(cd, "freeMemory");
            this.totalMemory = getLong(cd, "totalMemory");
            this.cpuUsedPercentage = getFloat(cd, "CPUUsedPercentage");
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

    public double[] getLoadAverages() {
        return loadAverages;
    }

    public void setLoadAverages(double[] loadAverages) {
        for (int i = 0; i < 3; i++) {
            this.loadAverages[i] = loadAverages[i];
        }
    }

    @Override
    public String toString() {
        return "cpu: " + cpuUsedPercentage + ", loadAverage: "
                + loadAverages[0] + ", " + loadAverages[1] + ", "
                + loadAverages[2] + ", Memory: " + freeMemory + "/"
                + totalMemory;
    }
}
