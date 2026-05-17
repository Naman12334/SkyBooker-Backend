package com.skybooker.seat.repository;

import com.skybooker.seat.entity.Seat;
import com.skybooker.seat.entity.SeatClass;
import com.skybooker.seat.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    // ── Basic queries ─────────────────────────────────────────────

    // Find all seats for a flight
    List<Seat> findByFlightId(int flightId);

    // Find seats by flight and class
    List<Seat> findByFlightIdAndSeatClass(int flightId,
            SeatClass seatClass);

    // Find seat by ID
    Optional<Seat> findBySeatId(int seatId);

    // Find seat by flight and seat number
    Optional<Seat> findByFlightIdAndSeatNumber(int flightId,
            String seatNumber);

    // Delete all seats for a flight
    void deleteByFlightId(int flightId);

    // ── Status queries ────────────────────────────────────────────

    // Find seats by status — used by SeatHoldScheduler
    List<Seat> findByStatus(SeatStatus status);

    // Find seats by flight and status
    List<Seat> findByFlightIdAndStatus(int flightId, SeatStatus status);

    // ── Available seat queries ────────────────────────────────────

    // Find available seats for a flight
    @Query("SELECT s FROM Seat s WHERE s.flightId = :flightId " +
           "AND s.status = 'AVAILABLE'")
    List<Seat> findAvailableByFlightId(
            @Param("flightId") int flightId);

    // Count available seats by class
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.flightId = :flightId " +
           "AND s.seatClass = :seatClass " +
           "AND s.status = 'AVAILABLE'")
    int countAvailableByClass(
            @Param("flightId") int flightId,
            @Param("seatClass") SeatClass seatClass);

    // ── Expired hold queries ──────────────────────────────────────

    // Find all HELD seats where hold has expired
    // Used by SeatHoldScheduler to auto release expired holds
    @Query("SELECT s FROM Seat s WHERE s.status = 'HELD' " +
           "AND s.holdExpiresAt < :now")
    List<Seat> findExpiredHolds(
            @Param("now") LocalDateTime now);
}