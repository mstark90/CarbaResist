/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.controllers;

import com.starkindustriesne.carbaresist.dto.JobDTO;
import com.starkindustriesne.carbaresist.model.Job;
import com.starkindustriesne.carbaresist.model.JobResult;
import com.starkindustriesne.carbaresist.model.JobStatus;
import com.starkindustriesne.carbaresist.repositories.JobRepository;
import com.starkindustriesne.carbaresist.repositories.JobResultRepository;
import com.starkindustriesne.carbaresist.services.JobManagerService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mstark
 */
@RestController
@RequestMapping("/jobs")
public class JobSubmissionController {
    @Autowired
    private JobRepository jobRepo;
    
    @Autowired
    private JobResultRepository jobResultRepo;
    
    @Autowired
    private JobManagerService jobManager;
    
    @PostMapping
    public Job createJob(
            @RequestBody @Valid JobDTO jobDto) {
        Job job = new Job();
        
        job.setJobStatus(JobStatus.SUBMITTED);
        job.setEmail(jobDto.getEmail());
        job.setJobName(jobDto.getJobName());
        job.setGenomeIds(new HashSet<>(Arrays.asList(jobDto.getGenomeIds().split("\n"))));
        job.setResistanceGeneIds(new HashSet<>(Arrays.asList(jobDto.getResistanceGeneIds())));
        job.setSubstitutionMatrix(jobDto.getSubstitutionMatrix());
        
        job = jobManager.sendJob(job);
        
        return job;
    }
    
    @GetMapping
    public List<Job> getAll() {
        return (List<Job>)jobRepo.findAll();
    }
    
    @GetMapping("/{jobId}")
    public Job getById(@PathVariable("jobId") String jobId) {
        return jobRepo.findOne(jobId);
    }
    
    @GetMapping("/{jobId}/result")
    public JobResult getResultByJobId(@PathVariable("jobId") String jobId) {
        return jobResultRepo.findByJobId(jobId);
    }
    
    @GetMapping("/email/{email:.+}")
    public List<Job> getByEmail(@PathVariable("email") String email,
            @RequestParam(value = "status", required = false) JobStatus status) {
        if(status != null) {
            return jobRepo.findByEmailAndJobStatus(email, status);
        } else {
            return jobRepo.findByEmail(email);
        }
    }
    
    @GetMapping("/status/{status}")
    public List<Job> getByStatus(@PathVariable("status") JobStatus status) {
        return jobRepo.findByJobStatus(status);
    }
}
