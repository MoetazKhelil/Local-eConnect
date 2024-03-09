package com.localeconnect.app.trip.rabbit;

import com.localeconnect.app.trip.dto.NotificationDTO;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RabbitMQMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publish(NotificationDTO payload, String exchange, String routingKey)  {
//        log.info("Publishing to {} using routingKey {}. Payload: {}", exchange, routingKey, payload);
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
//        log.info("Published to {} using routingKey {}. Payload: {}", exchange, routingKey, payload);
    }

}


