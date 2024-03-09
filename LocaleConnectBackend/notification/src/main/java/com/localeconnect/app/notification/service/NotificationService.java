package com.localeconnect.app.notification.service;

import com.localeconnect.app.notification.config.NotificationRabbitConfig;
import com.localeconnect.app.notification.dto.NotificationDTO;
import com.localeconnect.app.notification.mapper.NotificationMapper;
import com.localeconnect.app.notification.model.Notification;
import com.localeconnect.app.notification.rabbit.RabbitMQMessageProducer;
import com.localeconnect.app.notification.repository.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationMapper notificationMapper;

    private HashMap<String, String> connectedUsers;

    public NotificationDTO handleIncomingNotification(NotificationDTO notificationDTO) {

        Notification notification = notificationMapper.toEntity(notificationDTO);

        String receiverId = notificationDTO.getReceiverID().toString();

        //  Check if there exists a websocket connection with notification receiver.
        if (connectedUsers.containsValue(receiverId)) {
            log.info("CONNECTED USER: " + receiverId);
            notification.setRead(true);
            Notification createdNotification = notificationRepository.save(notification);
            NotificationDTO createdNotificationDTO = notificationMapper.toDomain(createdNotification);
            simpMessagingTemplate.convertAndSendToUser(receiverId, "/msg", createdNotificationDTO);
            return createdNotificationDTO;
        }

        log.info("NOT CONNECTED USER: " + receiverId);
        Notification createdNotification = notificationRepository.save(notification);
        NotificationDTO createdNotificationDTO = notificationMapper.toDomain(createdNotification);
        log.info("CREATED NOTIFICATION DTO: " + createdNotificationDTO);
        return createdNotificationDTO;
    }


    public void addUserToConnected(String sessionId, String userId) {
        connectedUsers.put(sessionId, userId);
    }

    public void removeUserFromConnected(String sessionId) {
        connectedUsers.remove(sessionId);
    }

    public List<NotificationDTO> getUnReadNotificationsByUserId(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByReceiverIDAndIsRead(userId, false);
        for (Notification notification : unreadNotifications) {
            notification.setRead(true); // Mark as read
        }
        List<Notification> readNotifications = notificationRepository.saveAll(unreadNotifications);

        return readNotifications.stream().map(notificationMapper::toDomain).toList();
    }
}
