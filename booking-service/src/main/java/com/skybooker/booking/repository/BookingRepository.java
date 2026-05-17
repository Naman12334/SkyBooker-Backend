package com.skybooker.booking.repository;

import com.skybooker.booking.entity.Booking;
import com.skybooker.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    // ── Basic queries ─────────────────────────────────────────────
    List<Booking> findByUserId(int userId);

    Optional<Booking> findByPnrCode(String pnrCode);

    List<Booking> findByFlightId(int flightId);

    List<Booking> findByStatus(BookingStatus status);

    Optional<Booking> findByBookingId(String bookingId);

    int countByFlightIdAndStatus(int flightId, BookingStatus status);

    List<Booking> findByUserIdAndStatus(int userId, BookingStatus status);

    // ── No-Show queries ───────────────────────────────────────────

    // Find all CONFIRMED bookings that were booked before a given time
    // Used by NoShowScheduler to find bookings whose flight has departed
    @Query("SELECT b FROM Booking b " +
           "WHERE b.status = 'CONFIRMED' " +
           "AND b.flightId = :flightId")
    List<Booking> findConfirmedBookingsByFlight(
            @Param("flightId") int flightId);

    // Find all CONFIRMED bookings booked before a certain datetime
    // Used to detect potential no-shows after departure
    @Query("SELECT b FROM Booking b " +
           "WHERE b.status = 'CONFIRMED' " +
           "AND b.bookedAt < :departureTime")
    List<Booking> findConfirmedBookingsBeforeTime(
            @Param("departureTime") LocalDateTime departureTime);

    // Find upcoming bookings for a user
    // bookedAt is after now and status is CONFIRMED
    @Query("SELECT b FROM Booking b " +
           "WHERE b.userId = :userId " +
           "AND b.status = 'CONFIRMED' " +
           "ORDER BY b.bookedAt DESC")
    List<Booking> findUpcomingBookings(
            @Param("userId") int userId);
}