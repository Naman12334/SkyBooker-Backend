package com.skybooker.booking.service;

import com.skybooker.booking.dto.FareSummary;
import com.skybooker.booking.entity.Booking;
import com.skybooker.booking.entity.BookingStatus;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    // Create booking with passenger list
    Booking createBooking(Booking booking,
            List<Integer> passengerIds);

    // Get booking by ID
    Optional<Booking> getBookingById(String bookingId);

    // Get booking by PNR
    Optional<Booking> getBookingByPnr(String pnrCode);

    // Get all bookings by user
    List<Booking> getBookingsByUser(int userId);

    // Get bookings by flight
    List<Booking> getBookingsByFlight(int flightId);

    // Cancel booking
    void cancelBooking(String bookingId);

    // Update booking status
    void updateStatus(String bookingId, BookingStatus status, String seatNumber);

    // Calculate fare
    FareSummary calculateFare(int flightId, int passengers);

    // Add add-on to booking
    void addAddOn(String bookingId, String addOnType,
            double amount);

    // Generate PNR code
    String generatePnr();

    // Get upcoming bookings for user
    List<Booking> getUpcomingBookings(int userId);

    // Get all bookings (Admin)
    List<Booking> getAllBookings();
}