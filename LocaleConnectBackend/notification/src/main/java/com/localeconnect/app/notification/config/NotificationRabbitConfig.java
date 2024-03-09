package com.localeconnect.app.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Configuration
@AllArgsConstructor
@Component
public class NotificationRabbitConfig {

    public static final String QUEUE = "notification.queue";
    public static final String EXCHANGE = "internal.exchange";
    public static final String ROUTING_KEY = "internal.notification.routing-key";

    @Bean
    public TopicExchange internalTopicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE);
    }

    @Bean
    public Binding internalToNotificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(internalTopicExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    public MessageConverter converter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        return converter;
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory, MessageConverter converter) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    public String getInternalExchange() {
        return EXCHANGE;
    }

    public String getNotificationQueue() {
        return QUEUE;
    }

    public String getInternalNotificationRoutingKey() {
        return ROUTING_KEY;
    }
}
