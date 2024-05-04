package com.uca.entity;

public class Depart {
    private int noLigne;
    private double heure;
    private int noTrain;

    public Depart(int noLigne, double heure, int noTrain) {
        this.noLigne = noLigne;
        this.heure = heure;
        this.noTrain = noTrain;
    }

    public int getNoLigne() {
        return noLigne;
    }

    public void setNoLigne(int noLigne) {
        this.noLigne = noLigne;
    }

    public double getHeure() {
        return heure;
    }

    public void setHeure(double heure) {
        this.heure = heure;
    }

    public int getNoTrain() {
        return noTrain;
    }

    public void setNoTrain(int noTrain) {
        this.noTrain = noTrain;
    }
}
