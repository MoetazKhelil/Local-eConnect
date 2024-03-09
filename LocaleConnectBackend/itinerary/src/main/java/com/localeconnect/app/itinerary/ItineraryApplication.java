package com.localeconnect.app.itinerary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ItineraryApplication {
    public static void main(String[] args) {
        SpringApplication.run(ItineraryApplication.class, args);
    }
}
