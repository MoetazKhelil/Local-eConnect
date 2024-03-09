package com.localeconnect.app.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/login",
            "/api/auth/register-traveler",
            "/api/auth/register-localguide"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
