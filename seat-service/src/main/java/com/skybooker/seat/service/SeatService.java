package com.skybooker.seat.service;

import com.skybooker.seat.entity.Seat;
import com.skybooker.seat.entity.SeatClass;

import java.util.List;
import java.util.Optional;

public interface SeatService {

    // ── Basic CRUD ────────────────────────────────────────────────

    // Add seats for a flight
    List<Seat> addSeatsForFlight(int flightId, List<Seat> seats);

    // Get all available seats for a flight
    List<Seat> getAvailableSeats(int flightId);

    // Get available seats by class
    List<Seat> getAvailableByClass(int flightId, SeatClass seatClass);

    // Get seat by ID
    Optional<Seat> getSeatById(int seatId);

    // Update seat details
    Seat updateSeat(int seatId, Seat seat);

    // Get seat map for a flight
    List<Seat> getSeatMap(int flightId);

    // Count available seats by class
    int countAvailableByClass(int flightId, SeatClass seatClass);

    // Delete all seats for a flight
    void deleteSeatsForFlight(int flightId);

    // ── Seat Hold Lifecycle with Optimistic Locking ───────────────

    // Hold a seat for 15 minutes during payment window
    // userId is stored to track who is holding the seat
    // Throws RuntimeException if seat already taken by another user
    void holdSeat(int seatId, int userId);

    // Release a held seat back to AVAILABLE
    void releaseSeat(int seatId);

    // Confirm a seat after successful payment
    void confirmSeat(int seatId);

    // Auto release all expired holds
    // Called by SeatHoldScheduler every 2 minutes
    void releaseExpiredHolds();
}