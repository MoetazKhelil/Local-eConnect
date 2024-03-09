package com.localeconnect.app.notification.controller;

import com.localeconnect.app.notification.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;

@Component
@AllArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private SimpMessageSendingOperations messagingTemplate;

    private NotificationService notificationService;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        String userId = headers.getNativeHeader("userId").get(0);
        log.info("USER CONNECTED WITH Session ID " + sessionId);
        log.info(headers.toString());
        notificationService.addUserToConnected(sessionId, userId);
    }


    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String sessionId = headers.getSessionId();
        notificationService.removeUserFromConnected(sessionId);
        log.info("USER DISCONNECTED with Session Id: " + sessionId);
        log.info(headers.toString());

    }
}
