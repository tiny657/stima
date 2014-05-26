package com.it.main;

import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = -6340576352497070080L;

    private int number;
    private String desc;

    public Command(int number, String desc) {
        this.number = number;
        this.desc = desc;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return desc + " " + number;
    }
}