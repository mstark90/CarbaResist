/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.dto;

import com.starkindustriesne.carbaresist.model.SubstitutionMatrix;
import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author mstark
 */
public class JobDTO implements Serializable {

    private static final long serialVersionUID = 5509959152285486809L;
    
    @NotNull
    @Size(min = 10, max = 200)
    private String jobName;
    
    @NotNull
    private String email;
    
    @NotNull
    private String genomeIds;
    
    @NotNull
    private String resistanceGeneIds;
    
    @NotNull
    private SubstitutionMatrix substitutionMatrix;

    /**
     * @return the name
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param name the name to set
     */
    public void setJobName(String name) {
        this.jobName = name;
    }

    /**
     * @return the genomeIds
     */
    public String getGenomeIds() {
        return genomeIds;
    }

    /**
     * @param genomeIds the genomeIds to set
     */
    public void setGenomeIds(String genomeIds) {
        this.genomeIds = genomeIds;
    }

    /**
     * @return the resistanceGeneIds
     */
    public String getResistanceGeneIds() {
        return resistanceGeneIds;
    }

    /**
     * @param resistanceGeneIds the resistanceGeneIds to set
     */
    public void setResistanceGeneIds(String resistanceGeneIds) {
        this.resistanceGeneIds = resistanceGeneIds;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the substitutionMatrix
     */
    public SubstitutionMatrix getSubstitutionMatrix() {
        return substitutionMatrix;
    }

    /**
     * @param substitutionMatrix the substitutionMatrix to set
     */
    public void setSubstitutionMatrix(SubstitutionMatrix substitutionMatrix) {
        this.substitutionMatrix = substitutionMatrix;
    }
}
