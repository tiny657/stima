package com.it.job;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class BandWidth implements Serializable {
    private static final long serialVersionUID = -474758079092837224L;

    private long recieved;
    private long sent;

    private long recivedPerSec;
    private long sentPerSec;

    public BandWidth adjust(BandWidth bandWidth) {
        recivedPerSec = recieved - bandWidth.getRecieved();
        sentPerSec = sent - bandWidth.getSent();
        return this;
    }

    public long getSentPerSec() {
        return sentPerSec;
    }

    public void setSentPerSec(long sentPerSec) {
        this.sentPerSec = sentPerSec;
    }

    public long getRecivedPerSec() {
        return recivedPerSec;
    }

    public void setRecivedPerSec(long recivedPerSec) {
        this.recivedPerSec = recivedPerSec;
    }

    public long getRecieved() {
        return recieved;
    }

    public void setRecieved(long recieved) {
        this.recieved = recieved;
    }

    public long getSent() {
        return sent;
    }

    public void setSent(long sent) {
        this.sent = sent;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}