package com.localeconnect.app.trip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class TripApplication {
    public static void main(String[] args) {
        SpringApplication.run(TripApplication.class, args);
    }
}
