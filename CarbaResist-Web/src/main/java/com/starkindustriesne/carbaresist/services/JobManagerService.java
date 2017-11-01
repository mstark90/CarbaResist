/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.services;

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

    @Autowired
    private JobRepository jobRepo;

    @Autowired
    private JobResultRepository jobResultRepo;

    @Autowired
    private RabbitTemplate messageSender;

    @Autowired
    private String initialQueueName;

    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        httpClient = HttpClientBuilder.create().build();
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
                content.append(line);
            }
        }

        return content.toString();
    }

    private String entrezSearch(String database, Iterable<String> ids) throws IOException {
        String url = String.format("https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?"
                + "db=%s&id=%s&rettype=fasta&retmode=text", database, String.join(",", ids));

        HttpGet request = new HttpGet(url);

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();

        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line);
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
                    
                    messageSender.convertAndSend(initialQueueName, task);
                }
            }
        } catch (Exception e) {
            job.setJobStatus(JobStatus.ERROR);
            result.setMessage(e.toString());
            
            job = jobRepo.save(job);
            result = jobResultRepo.save(result);
        }

        return job;
    }

    public void processResultEntry(JobResultEntry resultEntry) {
        JobResult result = jobResultRepo.findByJobResultId(resultEntry.getJobResultId());
        Job job = jobRepo.findByJobId(result.getJobId());

        result.getEntries().add(resultEntry);

        if (result.getEntryCount() == result.getEntries().size()) {
            result.setEnd(new Date());
            job.setJobStatus(JobStatus.DONE);
        }

        result = jobResultRepo.save(result);

    }
}
