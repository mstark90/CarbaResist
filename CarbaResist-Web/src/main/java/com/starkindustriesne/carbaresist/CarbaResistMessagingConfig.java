/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist;

import com.starkindustriesne.carbaresist.services.JobManagerService;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author mstark
 */
@Configuration
public class CarbaResistMessagingConfig {
    @Value("${rabbitmq.initialTaskQueueName}")
    private String initialQueue;
    
    @Value("${rabbitmq.finishedTaskQueueName}")
    private String finishedQueue;

    @Value("${rabbitmq.host}")
    private String messageHost;
    

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(messageHost);
    }

    @Bean
    public Queue queue() {
        return new Queue(initialQueue, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("carbaresist-exchange");
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(initialQueue);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
    
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(finishedQueueName());
        container.setMessageListener(listenerAdapter);
        return container;
    }
    
    @Bean
    public JobManagerService jobManager() {
        return new JobManagerService();
    }

    @Bean
    public MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(jobManager(), "processResultEntry");
    }

    /**
     * @return the initialQueue
     */
    @Bean
    public String initialQueueName() {
        return initialQueue;
    }

    /**
     * @return the finishedQueue
     */
    @Bean
    public String finishedQueueName() {
        return finishedQueue;
    }
}
