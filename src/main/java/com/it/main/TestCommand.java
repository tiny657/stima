package com.it.main;

import java.io.Serializable;

public class TestCommand implements Serializable {
    private static final long serialVersionUID = -595720457670882097L;
    private String desc;

    public TestCommand() {
        desc = "empty";
    }

    public TestCommand(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}