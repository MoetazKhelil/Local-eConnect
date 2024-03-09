package com.localeconnect.app.apigateway.config;


import com.localeconnect.app.apigateway.filter.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("chat-service", r -> r.path("/api/chat/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://chat-service:8003"))
                .route("meetup-service", r -> r.path("/api/meetup/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://meetup-service:7001"))
                .route("user-service-open", r -> r.path("/api/user/auth/**")
                        .uri("http://user-service:8084"))
                .route("user-service-secured", r -> r.path("/api/user/secured/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://user-service:8084"))
                .route("itinerary-service", r -> r.path("/api/itinerary/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://itinerary-service:8082"))
                .route("feed-service", r -> r.path("/api/feed/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://feed-service:8081"))
                .route("trip-service", r -> r.path("/api/trip/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://trip-service:8083"))
                .route("notification-service", r -> r.path("/api/notification/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://notification-service:7002"))
                .route("gcp-service", r -> r.path("/api/gcp/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://gcp-service:5005"))
                .route("notification-service", r -> r.path("/api/notification/**")
                        .filters(f -> f.filter(filter))
                        .uri("http://notification-service:7002"))
                .route("notification-websocket", r -> r.path("/ws/**")
//                        .filters(f -> f.filter(filter))
                        .uri("ws://notification-service:7002"))
                .build();
    }
}

