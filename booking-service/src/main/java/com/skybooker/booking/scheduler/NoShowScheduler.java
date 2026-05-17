package com.skybooker.booking.scheduler;

import com.skybooker.booking.entity.Booking;
import com.skybooker.booking.entity.BookingStatus;
import com.skybooker.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * NoShowScheduler
 *
 * Runs every 10 minutes and checks for bookings
 * where the flight has departed but passenger
 * never checked in — marks them as NO_SHOW.
 *
 * As per case study:
 * "Auto Cancel No-Show: Transition un-checked-in
 * bookings to NO_SHOW status after gate closure"
 */
@Component
public class NoShowScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(NoShowScheduler.class);

    @Autowired
    private BookingRepository bookingRepository;

    // ── Runs every 10 minutes ─────────────────────────────────────
    // cron = "0 */10 * * * *" means every 10 minutes
    @Scheduled(cron = "0 */10 * * * *")
    public void markNoShowBookings() {

        log.info("NoShowScheduler running at: {}", LocalDateTime.now());

        // Get current time minus 1 hour
        // Any CONFIRMED booking whose flight departed
        // more than 1 hour ago is a NO_SHOW
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        // Find all CONFIRMED bookings before 1 hour ago
        List<Booking> confirmedBookings = bookingRepository
                .findConfirmedBookingsBeforeTime(oneHourAgo);

        if (confirmedBookings.isEmpty()) {
            log.info("No NO_SHOW bookings found at this time.");
            return;
        }

        int noShowCount = 0;

        for (Booking booking : confirmedBookings) {
            // Mark as NO_SHOW
            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            noShowCount++;

            log.info("Marked NO_SHOW for booking: {} | User: {} | Flight: {}",
                    booking.getBookingId(),
                    booking.getUserId(),
                    booking.getFlightId());
        }

        log.info("NoShowScheduler completed. Total NO_SHOW marked: {}",
                noShowCount);
    }
}