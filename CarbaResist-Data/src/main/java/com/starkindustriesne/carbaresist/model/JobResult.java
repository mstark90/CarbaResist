package com.starkindustriesne.carbaresist.model;

import com.couchbase.client.java.repository.annotation.Field;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.couchbase.core.mapping.Document;

import com.couchbase.client.java.repository.annotation.Id;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

@Document
public class JobResult implements Serializable {

    /**
     * Default generated version ID.
     */
    private static final long serialVersionUID = -2800462412542674118L;

    @Id
    @Field
    @GeneratedValue(strategy = UNIQUE)
    private String jobResultId;
    
    @Field
    private String jobId;
    
    @Field
    private String message;

    @Field
    private Date start = new Date(), end = new Date();

    @Field
    private Set<JobResultEntry> entries = new HashSet<>();
    
    @Field
    private int entryCount;

    public String getJobResultId() {
        return jobResultId;
    }

    public void setJobResultId(String jobResultId) {
        this.jobResultId = jobResultId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Set<JobResultEntry> getEntries() {
        return entries;
    }

    public void setEntries(Set<JobResultEntry> entries) {
        this.entries = entries;
    }

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

    /**
     * @return the entryCount
     */
    public int getEntryCount() {
        return entryCount;
    }

    /**
     * @param entryCount the entryCount to set
     */
    public void setEntryCount(int entryCount) {
        this.entryCount = entryCount;
    }
}
