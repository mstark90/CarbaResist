/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starkindustriesne.carbaresist;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author mstark
 */
@Configuration
public class CarbaResistMessagingConfig {
    @Value("${rabbitmq.queueName}")
    private String queueName;

    @Value("${rabbitmq.host}")
    private String messageHost;
    
    @Bean
    public String queueName() {
        return queueName;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(messageHost);
    }

    @Bean
    public Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("carbaresist-exchange");
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue).to(topicExchange).with(queueName);
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }
}
