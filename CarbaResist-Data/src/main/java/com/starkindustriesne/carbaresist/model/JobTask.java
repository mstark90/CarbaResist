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
public class JobTask {
    private String jobId;
    private String jobResultId;
    
    private String genomeFasta;
    private String resistanceGeneFasta;

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the jobResultId
     */
    public String getJobResultId() {
        return jobResultId;
    }

    /**
     * @param jobResultId the jobResultId to set
     */
    public void setJobResultId(String jobResultId) {
        this.jobResultId = jobResultId;
    }

    /**
     * @return the genomeFasta
     */
    public String getGenomeFasta() {
        return genomeFasta;
    }

    /**
     * @param genomeFasta the genomeFasta to set
     */
    public void setGenomeFasta(String genomeFasta) {
        this.genomeFasta = genomeFasta;
    }

    /**
     * @return the resistanceGeneFasta
     */
    public String getResistanceGeneFasta() {
        return resistanceGeneFasta;
    }

    /**
     * @param resistanceGeneFasta the resistanceGeneFasta to set
     */
    public void setResistanceGeneFasta(String resistanceGeneFasta) {
        this.resistanceGeneFasta = resistanceGeneFasta;
    }
}
