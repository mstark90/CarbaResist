package com.starkindustriesne.carbaresist;

import com.starkindustriesne.carbaresist.services.JobProcessorService;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EnableAutoConfiguration()
public class CarbaResistProcessApplication {
    
    @Value("${rabbitmq.initialTaskQueueName}")
    private String initialQueue;
    
    @Value("${rabbitmq.finishedTaskQueueName}")
    private String finishedQueue;
    
    @Value("${rabbitmq.user}")
    private String amqpUsername;
    
    @Value("${rabbitmq.password}")
    private String amqpPassword;

    @Value("${rabbitmq.host}")
    private String messageHost;
    
    @Value("${carbaresist.substitutionMatrix}")
    private String substitutionMatrix;
    
    @Bean
    public String finishedQueue() {
        return finishedQueue;
    }
    
    @Bean
    public String aaSubstitutionMatrix() {
        return substitutionMatrix;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory(messageHost);
        
        factory.setUsername(amqpUsername);
        factory.setPassword(amqpPassword);
        
        return factory;
    }

    @Bean
    public Queue queue() {
        return new Queue(initialQueue, false, false, false);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("carbaresist-exchange");
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange topicExchange) {
        return BindingBuilder.bind(queue)
                .to(topicExchange).with(initialQueue);
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
        container.setQueueNames(initialQueue);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    
    @Bean
    public JobProcessorService jobProcessor() {
        return new JobProcessorService();
    }

    @Bean
    public MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(jobProcessor(), "processJobTask");
    }

    public static void main(String[] args) {
        SpringApplication.run(CarbaResistProcessApplication.class, args);
    }
}
