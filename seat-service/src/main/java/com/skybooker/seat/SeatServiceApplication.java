package com.skybooker.seat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SkyBooker Seat Service
 *
 * Manages seat inventory for each flight:
 * - Seat map management
 * - Hold/Release/Confirm lifecycle
 * - Optimistic locking to prevent double booking
 * - Auto release expired holds every 2 minutes
 */
@SpringBootApplication
@EnableScheduling
public class SeatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeatServiceApplication.class, args);
    }
}