package com.uca.entity;

public class Arret {
    private int noLigne;
    private int rang;
    private String ville;
    private double chrono;

    // Constructors
    public Arret(int noLigne, int rang, String ville, double chrono) {
        this.noLigne = noLigne;
        this.rang = rang;
        this.ville = ville;
        this.chrono = chrono;
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
}
