/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.model;

/**
 *
 * @author mstark
 */
public enum SubstitutionMatrix {
    BLOSUM_100("blosum100"),
    BLOSUM_95("blosum95"),
    BLOSUM_90("blosum90"),
    BLOSUM_85("blosum85"),
    BLOSUM_80("blosum80"),
    BLOSUM_75("blosum75"),
    BLOSUM_70("blosum70"),
    BLOSUM_65("blosum65"),
    BLOSUM_62("blosum62"),
    BLOSUM_60("blosum60"),
    BLOSUM_55("blosum55"),
    BLOSUM_50("blosum50"),
    BLOSUM_45("blosum45"),
    BLOSUM_40("blosum40"),
    BLOSUM_35("blosum35"),
    BLOSUM_30("blosum30"),
    PAM_250("pam250");
    
    private final String name;
    
    SubstitutionMatrix(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    
}
