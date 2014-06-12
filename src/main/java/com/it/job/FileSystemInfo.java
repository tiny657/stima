package com.it.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class FileSystemInfo implements Serializable {
    private static final long serialVersionUID = -8147779664020921043L;
    Sigar sigar;

    public static final class FileSystem {
        private final String deviceName;
        private final String mountPoint;
        private final long totalSizeKB;
        private final long freeSpaceKB;
        private final long readBytes;
        private final long writeBytes;

        public FileSystem( //
                String deviceName, String mountPoint, //
                String osSpecificFSType, //
                long totalSizeKB, long freeSpaceKB, long readBytes, long writeBytes) {
            this.deviceName = deviceName;
            this.mountPoint = mountPoint;
            this.totalSizeKB = totalSizeKB;
            this.freeSpaceKB = freeSpaceKB;
            this.readBytes = readBytes;
            this.writeBytes = writeBytes;
        }

        public static FileSystem fromSigarBean(org.hyperic.sigar.FileSystem fs,
                long totalSizeKB, long freeSpaceKB, long readBytes, long writeBytes) {
            return new FileSystem(fs.getDevName(), fs.getDirName(),
                    fs.getSysTypeName(), totalSizeKB, freeSpaceKB, readBytes, writeBytes);
        }

        public String deviceName() {
            return deviceName;
        }

        public String mountPoint() {
            return mountPoint;
        }

        public long totalSizeKB() {
            return totalSizeKB;
        }

        public long freeSpaceKB() {
            return freeSpaceKB;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public void setSigar(Sigar sigar) {
        this.sigar = sigar;
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
            long totalSizeKB = 0L;
            long freeSpaceKB = 0L;
            long readBytes = 0L;
            long writeBytes = 0L;
            try {
                FileSystemUsage usage = sigar.getFileSystemUsage(fs
                        .getDirName());
                totalSizeKB = usage.getTotal();
                freeSpaceKB = usage.getFree();
                readBytes = usage.getDiskReadBytes();
                writeBytes = usage.getDiskWriteBytes();
            } catch (SigarException e) {
                // ignore
            }
            result.add(FileSystem.fromSigarBean(fs, totalSizeKB, freeSpaceKB, readBytes, writeBytes));
        }
        return result;
    }
}
