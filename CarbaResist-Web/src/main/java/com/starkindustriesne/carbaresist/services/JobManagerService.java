/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starkindustriesne.carbaresist.model.Job;
import com.starkindustriesne.carbaresist.model.JobResult;
import com.starkindustriesne.carbaresist.model.JobResultEntry;
import com.starkindustriesne.carbaresist.model.JobStatus;
import com.starkindustriesne.carbaresist.model.JobTask;
import com.starkindustriesne.carbaresist.repositories.JobRepository;
import com.starkindustriesne.carbaresist.repositories.JobResultRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author mstark
 */
@Service
public class JobManagerService {
    
    private static final Logger logger = Logger.getLogger(JobManagerService.class.getName());

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private JobResultRepository jobResultRepo;

    @Autowired
    private RabbitTemplate messageSender;

    @Autowired
    private String initialQueueName;

    private HttpClient httpClient;
    
    private ObjectMapper serializer;

    @PostConstruct
    public void init() {
        httpClient = HttpClientBuilder.create().build();
        
        serializer = new ObjectMapper();
    }

    private String entrezSearch(String database, String id) throws IOException {
        String url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?"
                + "db=%s&id=%s&rettype=fasta&retmode=text", database, id);

        HttpGet request = new HttpGet(url);

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    private String entrezSearch(String database, String... ids) throws IOException {
        String url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?"
                + "db=%s&id=%s&rettype=fasta&retmode=text", database, String.join(",", ids));

        HttpGet request = new HttpGet(url);

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
    }

    public Job sendJob(Job job) {
        if (job.getJobStatus() != JobStatus.SUBMITTED) {
            return job;
        }

        job.setJobStatus(JobStatus.RUNNING);
        job = jobRepo.save(job);

        JobResult result = new JobResult();
        result.setJobId(job.getJobId());
        result.setEntryCount(job.getGenomeIds().size() * job.getResistanceGeneIds().size());
        result.setStart(new Date());

        result = jobResultRepo.save(result);

        try {
            for (String genomeId : job.getGenomeIds()) {
                for (String resistanceGeneId : job.getResistanceGeneIds()) {
                    String genome = entrezSearch("nuccore", genomeId);
                    String resistanceGene = entrezSearch("protein", resistanceGeneId);
                    
                    JobTask task = new JobTask();
                    
                    task.setJobId(job.getJobId());
                    task.setJobResultId(result.getJobResultId());
                    task.setGenomeFasta(genome);
                    task.setResistanceGeneFasta(resistanceGene);
                    task.setSubstitutionMatrix(job.getSubstitutionMatrix());
                    
                    messageSender.convertAndSend(initialQueueName, serializer.writeValueAsString(task));
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not process the job: ", e);
            
            job.setJobStatus(JobStatus.ERROR);
            result.setMessage(e.toString());
            
            job = jobRepo.save(job);
            result = jobResultRepo.save(result);
        }

        return job;
    }

    public void processResultEntry(String message) throws IOException {
        if(message.length() == 0) {
            return;
        }
        
        JobResultEntry resultEntry = serializer.readValue(message, JobResultEntry.class);
        
        if(resultEntry.getJobResultId() == null){
            return;
        }
        
        JobResult result = jobResultRepo.findOne(resultEntry.getJobResultId());
        Job job = jobRepo.findOne(result.getJobId());

        result.getEntries().add(resultEntry);

        if (result.getEntryCount() == result.getEntries().size()) {
            result.setEnd(new Date());
            job.setJobStatus(JobStatus.DONE);
        }

        result = jobResultRepo.save(result);
        jobRepo.save(job);
    }
}
