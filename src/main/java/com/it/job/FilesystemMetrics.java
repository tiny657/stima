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
        private final long readBytes;
        private final long writeBytes;

        public FileSystem(String deviceName, String osSpecificFSType,
                long totalSizeKB, long freeSpaceKB, long readBytes,
                long writeBytes) {
            this.deviceName = deviceName;
            this.totalSizeKB = totalSizeKB;
            this.freeSpaceKB = freeSpaceKB;
            this.readBytes = readBytes;
            this.writeBytes = writeBytes;
        }

        public static FileSystem fromSigarBean(org.hyperic.sigar.FileSystem fs,
                FileSystemUsage usage) {
            return new FileSystem(fs.getDevName(), fs.getSysTypeName(),
                    usage.getTotal(), usage.getFree(),
                    usage.getDiskReadBytes(), usage.getDiskWriteBytes());
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

        public long readBytes() {
            return readBytes;
        }

        public long writeBytes() {
            return writeBytes;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public List<FileSystem> filesystems() {
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
