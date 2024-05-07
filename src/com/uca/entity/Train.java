package com.uca.entity;

public class Train {

    private int no;
    private String type;
    private boolean onReserveTrack;
    private double longitude;
    private double latitude;
    private int noligne;

    public int getNoligne() {
        return noligne;
    }

    public void setNoligne(int noligne) {
        this.noligne = noligne;
    }

    public Train(int no, String type, boolean onReserveTrack, double longitude, double latitude, int noligne) {
        this.no = no;
        this.type = type;
        this.onReserveTrack = onReserveTrack;
        this.longitude = longitude;
        this.latitude = latitude;
        this.noligne = noligne;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isOnReserveTrack() {
        return onReserveTrack;
    }

    public void setOnReserveTrack(boolean onReserveTrack) {
        this.onReserveTrack = onReserveTrack;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
