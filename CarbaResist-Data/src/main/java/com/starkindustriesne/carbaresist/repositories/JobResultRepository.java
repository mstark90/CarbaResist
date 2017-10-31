package com.starkindustriesne.carbaresist.repositories;


import com.starkindustriesne.carbaresist.model.JobResult;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobResultRepository extends CouchbaseRepository<JobResult, String> {

    JobResult findByJobResultId(String resultId);
    
    JobResult findByJobId(String jobId);
}
