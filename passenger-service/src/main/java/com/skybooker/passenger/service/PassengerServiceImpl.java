package com.skybooker.passenger.service;

import com.skybooker.passenger.entity.PassengerInfo;
import com.skybooker.passenger.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PassengerServiceImpl implements PassengerService {

    @Autowired
    private PassengerRepository passengerRepository;

    // ── Add Passenger ─────────────────────────────────────────────
    @Override
    public PassengerInfo addPassenger(PassengerInfo passengerInfo) {
        // Generate ticket number before saving
        passengerInfo.setTicketNumber(generateTicketNumber());
        return passengerRepository.save(passengerInfo);
    }

    // ── Get Passenger By ID ───────────────────────────────────────
    @Override
    public Optional<PassengerInfo> getPassengerById(int passengerId) {
        return passengerRepository.findById(passengerId);
    }

    // ── Get Passengers By Booking ─────────────────────────────────
    @Override
    public List<PassengerInfo> getPassengersByBooking(String bookingId) {
        return passengerRepository.findByBookingId(bookingId);
    }

    // ── Get By Passport Number ────────────────────────────────────
    @Override
    public Optional<PassengerInfo> getByPassportNumber(
            String passportNumber) {
        return passengerRepository.findByPassportNumber(passportNumber);
    }

    // ── Update Passenger ──────────────────────────────────────────
    @Override
    public PassengerInfo updatePassenger(int passengerId,
                                          PassengerInfo passengerInfo) {
        PassengerInfo existing = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException(
                        "Passenger not found: " + passengerId));

        existing.setTitle(passengerInfo.getTitle());
        existing.setFirstName(passengerInfo.getFirstName());
        existing.setLastName(passengerInfo.getLastName());
        existing.setDateOfBirth(passengerInfo.getDateOfBirth());
        existing.setGender(passengerInfo.getGender());
        existing.setPassportNumber(passengerInfo.getPassportNumber());
        existing.setNationality(passengerInfo.getNationality());
        existing.setPassportExpiry(passengerInfo.getPassportExpiry());
        existing.setMealPreference(passengerInfo.getMealPreference());
        existing.setPassengerType(passengerInfo.getPassengerType());

        return passengerRepository.save(existing);
    }

    // ── Assign Seat ───────────────────────────────────────────────
    @Override
    public void assignSeat(int passengerId, String seatNumber) {
        PassengerInfo passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException(
                        "Passenger not found: " + passengerId));
        passenger.setSeatNumber(seatNumber);
        passengerRepository.save(passenger);
    }

    // ── Generate Ticket Number ────────────────────────────────────
    @Override
    public String generateTicketNumber() {
        // Format: SKY-XXXXXXXXXXXXXXXX (16 chars after SKY-)
        return "SKY-" + UUID.randomUUID()
                .toString().replace("-", "")
                .substring(0, 16).toUpperCase();
    }

    // ── Delete Passenger ──────────────────────────────────────────
    @Override
    public void deletePassenger(int passengerId) {
        passengerRepository.deleteById(passengerId);
    }

    // ── Validate Passenger Data ───────────────────────────────────
    @Override
    public boolean validatePassengerData(PassengerInfo passengerInfo) {

        // Check required fields
        if (passengerInfo.getFirstName() == null
                || passengerInfo.getFirstName().isEmpty()) {
            throw new RuntimeException("First name is required");
        }

        if (passengerInfo.getLastName() == null
                || passengerInfo.getLastName().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }

        // Check passport expiry — must not be expired
        if (passengerInfo.getPassportExpiry() != null
                && passengerInfo.getPassportExpiry()
                .isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException(
                    "Passport is expired for passenger: "
                    + passengerInfo.getFirstName());
        }

        return true;
    }

    // ── Get Passenger Count ───────────────────────────────────────
    @Override
    public int getPassengerCount(String bookingId) {
        return passengerRepository.countByBookingId(bookingId);
    }

    // ── Web Check-In ──────────────────────────────────────────────
    @Override
    public PassengerInfo webCheckIn(int passengerId,
                                     String bookingId,
                                     String departureDateTimeStr) {

        PassengerInfo passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException(
                        "Passenger not found: " + passengerId));

        // Check if already checked in
        if (passenger.isCheckedIn()) {
            throw new RuntimeException(
                    "Passenger already checked in: " + passengerId);
        }

        // Check if passenger belongs to this booking
        if (!passenger.getBookingId().equals(bookingId)) {
            throw new RuntimeException(
                    "Passenger does not belong to booking: " + bookingId);
        }

        // Validate check-in window
        // Check-in opens 24 hours before departure
        // Check-in closes 1 hour before departure
        LocalDateTime departureDateTime = LocalDateTime.parse(
                departureDateTimeStr,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInOpens  = departureDateTime.minusHours(24);
        LocalDateTime checkInCloses = departureDateTime.minusHours(1);

        if (now.isBefore(checkInOpens)) {
            throw new RuntimeException(
                    "Check-in not open yet. Opens at: " + checkInOpens);
        }

        if (now.isAfter(checkInCloses)) {
            throw new RuntimeException(
                    "Check-in closed. Closed at: " + checkInCloses);
        }

        // Check if seat is assigned
        if (passenger.getSeatNumber() == null
                || passenger.getSeatNumber().isEmpty()) {
            throw new RuntimeException(
                    "No seat assigned. Please select a seat first.");
        }

        // Perform check-in
        passenger.setCheckedIn(true);
        passenger.setCheckInTime(now);

        return passengerRepository.save(passenger);
    }

    // ── Get Checked In Passengers ─────────────────────────────────
    @Override
    public List<PassengerInfo> getCheckedInPassengers(String bookingId) {
        return passengerRepository
                .findByBookingIdAndIsCheckedIn(bookingId, true);
    }

    // ── Get Check-In Status ───────────────────────────────────────
    @Override
    public boolean getCheckInStatus(int passengerId) {
        PassengerInfo passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException(
                        "Passenger not found: " + passengerId));
        return passenger.isCheckedIn();
    }

    // ── Get Checked In Count ──────────────────────────────────────
    @Override
    public int getCheckedInCount(String bookingId) {
        return passengerRepository
                .countByBookingIdAndCheckedIn(bookingId, true);
    }

    // ── Generate Boarding Pass ────────────────────────────────────
    @Override
    public String generateBoardingPass(int passengerId) {

        PassengerInfo passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException(
                        "Passenger not found: " + passengerId));

        // Must be checked in first
        if (!passenger.isCheckedIn()) {
            throw new RuntimeException(
                    "Passenger must complete web check-in first.");
        }

        // Mark boarding pass as generated
        passenger.setBoardingPassGenerated(true);
        passengerRepository.save(passenger);

        // Return boarding pass as formatted string
        // In production: generate PDF using iText7
        return String.format(
                "╔══════════════════════════════════════╗\n" +
                "║         SKYBOOKER BOARDING PASS      ║\n" +
                "╠══════════════════════════════════════╣\n" +
                "║ Passenger : %-25s║\n" +
                "║ Ticket No : %-25s║\n" +
                "║ Booking ID: %-25s║\n" +
                "║ Seat      : %-25s║\n" +
                "║ Check-In  : %-25s║\n" +
                "╚══════════════════════════════════════╝",
                passenger.getFirstName() + " " + passenger.getLastName(),
                passenger.getTicketNumber(),
                passenger.getBookingId(),
                passenger.getSeatNumber(),
                passenger.getCheckInTime()
        );
    }
}