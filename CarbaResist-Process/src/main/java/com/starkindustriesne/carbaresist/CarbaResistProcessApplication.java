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
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CarbaResistDBConfig.class})
public class CarbaResistProcessApplication {
    
    @Value("${rabbitmq.queueName}")
    private String queueName;

    @Value("${rabbitmq.host}")
    private String messageHost;
    
    @Value("${carbaresist.substitutionMatrix}")
    private String substitutionMatrix;
    
    @Bean
    public String queueName() {
        return queueName;
    }
    
    @Bean
    public String aaSubstitutionMatrix() {
        return substitutionMatrix;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(messageHost);
    }

    @Bean
    public Queue queue() {
        return new Queue(queueName, false, true, true);
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
    
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    
    @Bean
    public JobProcessorService jobProcessor() {
        return new JobProcessorService();
    }

    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(jobProcessor(), "processJob");
    }

    public static void main(String[] args) {
        SpringApplication.run(CarbaResistProcessApplication.class, args);
    }
}
