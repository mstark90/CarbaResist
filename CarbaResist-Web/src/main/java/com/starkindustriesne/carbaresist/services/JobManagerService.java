/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist.services;

import com.starkindustriesne.carbaresist.model.Job;
import com.starkindustriesne.carbaresist.repositories.JobRepository;
import com.starkindustriesne.carbaresist.repositories.JobResultRepository;
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
    private JobResultRepository jobResultRepository;
    
    @Autowired
    private RabbitTemplate messageSender;
    
    @Autowired
    private String queueName;
    
    public Job sendJob(Job job) {
        Job j = jobRepo.save(job);
        
        messageSender.convertAndSend(queueName, j);
        
        return j;
    }
    
}
