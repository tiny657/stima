package com.it.job;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class FilesystemMetrics extends AbstractSigarMetric {
    protected FilesystemMetrics(Sigar sigar) {
        super(sigar);
    }

    public static final class FileSystem {
        private final String deviceName;
        private final long totalSizeKB;
        private final long freeSpaceKB;
        private final long rxBytes;
        private final long txBytes;

        public FileSystem(String deviceName, long totalSizeKB,
                long freeSpaceKB, long rxBytes, long txBytes) {
            this.deviceName = deviceName;
            this.totalSizeKB = totalSizeKB;
            this.freeSpaceKB = freeSpaceKB;
            this.rxBytes = rxBytes;
            this.txBytes = txBytes;
        }

        public static FileSystem fromSigarBean(org.hyperic.sigar.FileSystem fs,
                FileSystemUsage usage) {
            return new FileSystem(fs.getDevName(), usage.getTotal(),
                    usage.getFree(), usage.getDiskReadBytes(),
                    usage.getDiskWriteBytes());
        }

        public FileSystem diff(FileSystem fileSystem) {
            return new FileSystem(deviceName, totalSizeKB, freeSpaceKB, txBytes
                    - fileSystem.txBytes(), rxBytes - fileSystem.rxBytes());
        }

        public String deviceName() {
            return deviceName;
        }

        public long totalSizeKB() {
            return totalSizeKB;
        }

        public long freeSpaceKB() {
            return freeSpaceKB;
        }

        public long rxBytes() {
            return rxBytes;
        }

        public long txBytes() {
            return txBytes;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public List<FileSystem> fileSystems() {
        List<FileSystem> result = new ArrayList<FileSystem>();
        org.hyperic.sigar.FileSystem[] fss = null;
        try {
            fss = sigar.getFileSystemList();
        } catch (SigarException e) {
            // give up
            return result;
        }

        if (fss == null) {
            return result;
        }

        for (org.hyperic.sigar.FileSystem fs : fss) {
            try {
                FileSystemUsage usage = sigar.getFileSystemUsage(fs
                        .getDirName());
                result.add(FileSystem.fromSigarBean(fs, usage));
            } catch (SigarException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
