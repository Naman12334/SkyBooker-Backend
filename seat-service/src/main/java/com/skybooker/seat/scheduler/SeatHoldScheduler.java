package com.skybooker.seat.scheduler;

import com.skybooker.seat.service.SeatServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * SeatHoldScheduler
 *
 * Runs every 2 minutes and releases any seat holds
 * that have expired (15 minute hold window passed
 * without payment being completed).
 *
 * As per case study:
 * "Seat holds expire after 15 minutes if payment
 * is not completed. A Spring @Scheduled job runs
 * every 2 minutes to release expired holds."
 */
@Component
public class SeatHoldScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(SeatHoldScheduler.class);

    @Autowired
    private SeatServiceImpl seatService;

    // ── Runs every 2 minutes ──────────────────────────────────────
    // fixedRate = 120000 means every 120 seconds = 2 minutes
    @Scheduled(fixedRate = 120000)
    public void releaseExpiredHolds() {
        log.info("SeatHoldScheduler running at: {}",
                LocalDateTime.now());

        try {
            seatService.releaseExpiredHolds();
            log.info("SeatHoldScheduler completed successfully.");
        } catch (Exception e) {
            log.error("SeatHoldScheduler error: {}", e.getMessage());
        }
    }
}