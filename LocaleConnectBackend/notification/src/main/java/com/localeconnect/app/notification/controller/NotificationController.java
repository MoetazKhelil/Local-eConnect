package com.localeconnect.app.notification.controller;

import com.localeconnect.app.notification.config.NotificationRabbitConfig;
import com.localeconnect.app.notification.dto.NotificationDTO;
import com.localeconnect.app.notification.rabbit.RabbitMQMessageProducer;
import com.localeconnect.app.notification.response_handler.ResponseHandler;
import com.localeconnect.app.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private SimpMessagingTemplate messagingTemplate;

    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    @GetMapping("/notify/{userId}")
    public ResponseEntity<Object> getNotification(@PathVariable("userId") Long userId) throws InterruptedException {
        List<NotificationDTO> notifications = notificationService.getUnReadNotificationsByUserId(userId);
        return ResponseHandler.generateResponse("Success!", HttpStatus.OK, notifications, null);
    }
}