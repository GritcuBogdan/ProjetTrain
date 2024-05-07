package com.uca.entity;

public class Arret {
    private int noLigne;
    private int rang;
    private String ville;
    private double chrono;
    private int reserveDesPistes;
    private double longitude;
    private double latitude;

    // Constructors
    public Arret(int noLigne, int rang, String ville, double chrono, int reserveDesPistes, double longitude, double latitude) {
        this.noLigne = noLigne;
        this.rang = rang;
        this.ville = ville;
        this.chrono = chrono;
        this.reserveDesPistes = reserveDesPistes;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and setters
    public int getNoLigne() {
        return noLigne;
    }

    public void setNoLigne(int noLigne) {
        this.noLigne = noLigne;
    }

    public int getRang() {
        return rang;
    }

    public void setRang(int rang) {
        this.rang = rang;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public double getChrono() {
        return chrono;
    }

    public void setChrono(double chrono) {
        this.chrono = chrono;
    }

    public int getReserveDesPistes() {
        return reserveDesPistes;
    }

    public void setReserveDesPistes(int reserveDesPistes) {
        this.reserveDesPistes = reserveDesPistes;
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
