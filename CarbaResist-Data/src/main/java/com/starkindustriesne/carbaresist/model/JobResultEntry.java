package com.starkindustriesne.carbaresist.model;

import java.io.Serializable;

public class JobResultEntry implements Serializable {

    /**
     * Generated version ID.
     */
    private static final long serialVersionUID = 8789831573078632924L;

    private String entryId, jobResultId, genomeId, resistanceGeneId, alignment,
            message;

    private int score = 0;

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getResistanceGeneId() {
        return resistanceGeneId;
    }

    public void setResistanceGeneId(String resistanceGeneId) {
        this.resistanceGeneId = resistanceGeneId;
    }

    public String getGenomeId() {
        return genomeId;
    }

    public void setGenomeId(String genomeId) {
        this.genomeId = genomeId;
    }

    public String getJobResultId() {
        return jobResultId;
    }

    public void setJobResultId(String jobResultId) {
        this.jobResultId = jobResultId;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
