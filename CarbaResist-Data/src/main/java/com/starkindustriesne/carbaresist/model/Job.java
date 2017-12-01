package com.starkindustriesne.carbaresist.model;

import com.couchbase.client.java.repository.annotation.Field;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Id;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

@Document
public class Job implements Serializable {

    /**
     * Generated version ID.
     */
    private static final long serialVersionUID = 3427294993975615880L;

    @Id
    @Field
    @GeneratedValue(strategy = UNIQUE)
    private String jobId;
    
    @Field
    private String jobName;
    
    @Field
    private String email;
    
    @Field
    private SubstitutionMatrix substitutionMatrix;

    @Field
    private Set<String> genomeIds = new HashSet<>(), resistanceGeneIds = new HashSet<>();

    @Field
    private JobStatus jobStatus;

    public Set<String> getResistanceGeneIds() {
        return resistanceGeneIds;
    }

    public void setResistanceGeneIds(Set<String> resistanceGeneIds) {
        this.resistanceGeneIds = resistanceGeneIds;
    }

    public Set<String> getGenomeIds() {
        return genomeIds;
    }

    public void setGenomeIds(Set<String> genomeIds) {
        this.genomeIds = genomeIds;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName the jobName to set
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
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
    
    public SubstitutionMatrix getSubstitutionMatrix() {
        return substitutionMatrix;
    }

    public void setSubstitutionMatrix(SubstitutionMatrix substitutionMatrix) {
        this.substitutionMatrix = substitutionMatrix;
    }
}
