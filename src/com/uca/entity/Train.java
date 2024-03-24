package com.uca.entity;

public class Train {

    private int no;
    private String type;

    public Train(int no, String type) {
        this.no = no;
        this.type = type;
    }

    public int getNo() {
        return this.no;
    }

    public String getType() {
        return this.type;
    }
}
