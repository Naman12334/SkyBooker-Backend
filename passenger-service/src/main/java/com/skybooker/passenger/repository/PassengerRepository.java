package com.skybooker.passenger.repository;

import com.skybooker.passenger.entity.PassengerInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PassengerRepository extends JpaRepository<PassengerInfo, Integer> {

    // ── Basic queries ─────────────────────────────────────────────
    List<PassengerInfo> findByBookingId(String bookingId);

    Optional<PassengerInfo> findByPassengerId(int passengerId);

    Optional<PassengerInfo> findByPassportNumber(String passportNumber);

    Optional<PassengerInfo> findByTicketNumber(String ticketNumber);

    Optional<PassengerInfo> findBySeatId(int seatId);

    int countByBookingId(String bookingId);

    void deleteByBookingId(String bookingId);

    // ── Check-In queries ──────────────────────────────────────────

    // Use @Query instead of method name to avoid Lombok boolean issue
    @Query("SELECT p FROM PassengerInfo p " +
           "WHERE p.bookingId = :bookingId " +
           "AND p.isCheckedIn = :isCheckedIn")
    List<PassengerInfo> findByBookingIdAndIsCheckedIn(
            @Param("bookingId") String bookingId,
            @Param("isCheckedIn") boolean isCheckedIn);

    @Query("SELECT COUNT(p) FROM PassengerInfo p " +
           "WHERE p.bookingId = :bookingId " +
           "AND p.isCheckedIn = :isCheckedIn")
    int countByBookingIdAndCheckedIn(
            @Param("bookingId") String bookingId,
            @Param("isCheckedIn") boolean isCheckedIn);

    @Query("SELECT p FROM PassengerInfo p " +
           "WHERE p.bookingId IN :bookingIds " +
           "AND p.isCheckedIn = true")
    List<PassengerInfo> findCheckedInByBookingIds(
            @Param("bookingIds") List<String> bookingIds);

    @Query("SELECT p FROM PassengerInfo p " +
           "WHERE p.bookingId = :bookingId " +
           "AND p.seatNumber = :seatNumber")
    Optional<PassengerInfo> findByBookingIdAndSeatNumber(
            @Param("bookingId") String bookingId,
            @Param("seatNumber") String seatNumber);
}