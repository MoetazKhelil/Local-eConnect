package com.localeconnect.app.notification.rabbit;

import com.localeconnect.app.notification.config.NotificationRabbitConfig;
import com.localeconnect.app.notification.dto.NotificationDTO;
import com.localeconnect.app.notification.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = NotificationRabbitConfig.QUEUE)
    public void notificationConsumer(NotificationDTO notificationDTO) {

        log.info("Notification Received in queue: " + NotificationRabbitConfig.QUEUE + " with content:  " + notificationDTO);
//        messagingTemplate.convertAndSend("/topic/notification", notificationDTO);
        notificationService.handleIncomingNotification(notificationDTO);
    }

}
