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
    BLOSUM_90("blosum90"),
    BLOSUM_80("blosum80"),
    BLOSUM_60("blosum60"),
    BLOSUM_52("blosum52"),
    BLOSUM_45("blosum45"),
    PAM_100("pam100"),
    PAM_120("pam120"),
    PAM_160("pam160"),
    PAM_200("pam200"),
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
