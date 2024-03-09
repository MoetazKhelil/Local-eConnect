package com.localeconnect.app.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Client can connect to this service using this endpoint over websocket connection.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        registry.addEndpoint("/ws/info").setAllowedOrigins("*");
        registry.addEndpoint("/ws/info")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // "/topic" - By creating any endpoint that starts with this root (like /topic/newMember or /topic/disconnectedMember)
        // Spring will send the messages to any connected user in the WebSocket

        // "/queue" - By creating any endpoint that starts with this root (like /queue/register or /queue/unregister)
        // Spring will send the messages only to the user who requested it, like a normal HTTP url.
        // Imagine you want to register to our chat and if you are eligible you want to receive the list of the other
        // connected users but the other users already have this list, so you don't want to send them anything.

        // "/user" - By creating any endpoint that starts with this root (like /user/{username}/msg)
        // Spring will send the messages only to the user in the brackets({username}).
        // Notice that when we implement the /user routes we won't need user {username}, it was just to ilustrate my example.
//         This adds a message broker that clients can subscribe to and listen to the messages
//         which our service(backend) is sending back.
        registry.enableSimpleBroker("/topic", "/queue", "/user");

//        The "/api/notification" in setApplicationDestinationPrefixes method call is a random name you can give
//        to segregate your WebSocket routes from normal Http routes.
        registry.setApplicationDestinationPrefixes("/api/notification");

    }

}
