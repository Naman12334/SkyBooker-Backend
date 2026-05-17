package com.skybooker.passenger.service;

import com.skybooker.passenger.entity.PassengerInfo;

import java.util.List;
import java.util.Optional;

public interface PassengerService {

    // ── Basic CRUD ────────────────────────────────────────────────
    PassengerInfo addPassenger(PassengerInfo passengerInfo);

    Optional<PassengerInfo> getPassengerById(int passengerId);

    List<PassengerInfo> getPassengersByBooking(String bookingId);

    Optional<PassengerInfo> getByPassportNumber(String passportNumber);

    PassengerInfo updatePassenger(int passengerId,
                                  PassengerInfo passengerInfo);

    void assignSeat(int passengerId, String seatNumber);

    String generateTicketNumber();

    void deletePassenger(int passengerId);

    boolean validatePassengerData(PassengerInfo passengerInfo);

    int getPassengerCount(String bookingId);

    // ── Web Check-In ──────────────────────────────────────────────

    // Perform web check-in for a passenger
    // Returns updated PassengerInfo with isCheckedIn = true
    PassengerInfo webCheckIn(int passengerId, String bookingId,
                              String departureDateTimeStr);

    // Get all checked in passengers for a booking
    List<PassengerInfo> getCheckedInPassengers(String bookingId);

    // Get check-in status for a passenger
    boolean getCheckInStatus(int passengerId);

    // Get count of checked in passengers for a booking
    int getCheckedInCount(String bookingId);

    // Generate boarding pass data for a passenger
    String generateBoardingPass(int passengerId);
}