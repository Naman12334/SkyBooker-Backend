package com.skybooker.booking.service;

import com.skybooker.booking.dto.FareSummary;
import com.skybooker.booking.entity.Booking;
import com.skybooker.booking.entity.BookingStatus;
import com.skybooker.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking,
            List<Integer> passengerIds) {
        // Generate unique booking ID
        booking.setBookingId(UUID.randomUUID().toString());
        // Generate unique PNR
        booking.setPnrCode(generatePnr());
        // Set initial status
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    @Override
    public Optional<Booking> getBookingById(String bookingId) {
        return bookingRepository.findByBookingId(bookingId);
    }

    @Override
    public Optional<Booking> getBookingByPnr(String pnrCode) {
        return bookingRepository.findByPnrCode(pnrCode);
    }

    @Override
    public List<Booking> getBookingsByUser(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<Booking> getBookingsByFlight(int flightId) {
        return bookingRepository.findByFlightId(flightId);
    }

    @Override
    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Booking not found!"));
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException(
                    "Booking is already cancelled!");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException(
                    "Cannot cancel completed booking!");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public void updateStatus(String bookingId,
            BookingStatus status, String seatNumber) {
        Booking booking = bookingRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Booking not found!"));
        booking.setStatus(status);
        if (seatNumber != null) {
            booking.setSeatNumber(seatNumber);
        }
        bookingRepository.save(booking);
    }

    @Override
    public FareSummary calculateFare(int flightId,
            int passengers) {
        // Base fare calculation
        // In real world this would call flight-service
        double baseFare = 3500.0 * passengers;
        double taxes = baseFare * 0.18; // 18% GST
        double fuelSurcharge = baseFare * 0.05; // 5% fuel
        double totalFare = baseFare + taxes + fuelSurcharge;

        FareSummary fareSummary = new FareSummary();
        fareSummary.setBaseFare(baseFare);
        fareSummary.setTaxes(taxes);
        fareSummary.setFuelSurcharge(fuelSurcharge);
        fareSummary.setMealCharges(0.0);
        fareSummary.setBaggageCharges(0.0);
        fareSummary.setTotalFare(totalFare);
        return fareSummary;
    }

    @Override
    public void addAddOn(String bookingId,
            String addOnType, double amount) {
        Booking booking = bookingRepository
                .findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException(
                        "Booking not found!"));
        if (addOnType.equalsIgnoreCase("MEAL")) {
            booking.setMealPreference(addOnType);
            booking.setTotalFare(
                    booking.getTotalFare() + amount);
        } else if (addOnType.equalsIgnoreCase("BAGGAGE")) {
            booking.setLuggageKg(
                    booking.getLuggageKg() + amount);
            booking.setTotalFare(
                    booking.getTotalFare() + (amount * 500));
        }
        bookingRepository.save(booking);
    }

    @Override
    public String generatePnr() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder pnr = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            pnr.append(chars.charAt(
                    random.nextInt(chars.length())));
        }
        // Check uniqueness
        String pnrCode = pnr.toString();
        while (bookingRepository.findByPnrCode(pnrCode)
                .isPresent()) {
            pnrCode = generatePnr();
        }
        return pnrCode;
    }

    @Override
    public List<Booking> getUpcomingBookings(int userId) {
        return bookingRepository
                .findByUserIdAndStatus(userId,
                        BookingStatus.CONFIRMED);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}