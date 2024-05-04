package com.uca.entity;

public class Ligne {
    private int noLigne;
    private String nom;

    public Ligne(int noLigne, String nom) {
        this.noLigne = noLigne;
        this.nom = nom;
    }

    // Getters and setters
    public int getNoLigne() {
        return noLigne;
    }

    public void setNoLigne(int noLigne) {
        this.noLigne = noLigne;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
