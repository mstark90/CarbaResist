package com.starkindustriesne.carbaresist.repositories;


import com.starkindustriesne.carbaresist.model.Job;
import com.starkindustriesne.carbaresist.model.JobStatus;
import java.util.List;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends CouchbaseRepository<Job, String> {

    Job findByJobId(String jobId);

    List<Job> findByJobStatus(JobStatus jobStatus);
    
    List<Job> findByEmail(String email);
    
    List<Job> findByEmailAndJobStatus(String email, JobStatus jobStatus);
}
