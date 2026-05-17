package com.skybooker.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SkyBooker Booking Service
 *
 * Central orchestration service for:
 * - Booking lifecycle management
 * - PNR generation
 * - Fare calculation
 * - No-Show detection (scheduled job)
 */
@SpringBootApplication
@EnableScheduling
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}