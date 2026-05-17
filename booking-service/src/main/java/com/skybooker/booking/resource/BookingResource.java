package com.skybooker.booking.resource;

import com.skybooker.booking.dto.FareSummary;
import com.skybooker.booking.entity.Booking;
import com.skybooker.booking.entity.BookingStatus;
import com.skybooker.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
public class BookingResource {

    @Autowired
    private BookingService bookingService;

    // POST /bookings
    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody Map<String, Object> request) {
        Booking booking = new Booking();
        booking.setUserId((Integer) request.get("userId"));
        booking.setFlightId((Integer) request.get("flightId"));
        booking.setContactEmail(
                (String) request.get("contactEmail"));
        booking.setContactPhone(
                (String) request.get("contactPhone"));
        booking.setBaseFare(
                ((Number) request.get("baseFare")).doubleValue());
        booking.setTaxes(
                ((Number) request.get("taxes")).doubleValue());
        booking.setTotalFare(
                ((Number) request.get("totalFare")).doubleValue());

        // Handle tripType
        String tripType = (String) request.get("tripType");
        booking.setTripType(
                com.skybooker.booking.entity.TripType
                        .valueOf(tripType));

        // Handle returnFlightId for round trip
        if (request.get("returnFlightId") != null) {
            booking.setReturnFlightId(
                    (Integer) request.get("returnFlightId"));
        }

        // Handle meal preference
        if (request.get("mealPreference") != null) {
            booking.setMealPreference(
                    (String) request.get("mealPreference"));
        }

        // Handle luggage
        if (request.get("luggageKg") != null) {
            booking.setLuggageKg(
                    ((Number) request.get("luggageKg"))
                            .doubleValue());
        }

        @SuppressWarnings("unchecked")
        List<Integer> passengerIds =
                (List<Integer>) request.get("passengerIds");

        Booking created = bookingService.createBooking(
                booking, passengerIds);
        return ResponseEntity.ok(created);
    }

    // GET /bookings/{bookingId}
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(
            @PathVariable String bookingId) {
        return bookingService.getBookingById(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /bookings/pnr/{pnrCode}
    @GetMapping("/pnr/{pnrCode}")
    public ResponseEntity<Booking> getBookingByPnr(
            @PathVariable String pnrCode) {
        return bookingService.getBookingByPnr(pnrCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /bookings/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(
            @PathVariable int userId) {
        List<Booking> bookings = bookingService
                .getBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }

    // GET /bookings/flight/{flightId}
    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Booking>> getBookingsByFlight(
            @PathVariable int flightId) {
        List<Booking> bookings = bookingService
                .getBookingsByFlight(flightId);
        return ResponseEntity.ok(bookings);
    }

    // PUT /bookings/{bookingId}/cancel
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable String bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(
                "Booking cancelled successfully!");
    }

    // PUT /bookings/{bookingId}/status
    @PutMapping("/{bookingId}/status")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN') or hasRole('AIRLINE_STAFF')")
    public ResponseEntity<String> updateStatus(
            @PathVariable String bookingId,
            @RequestParam BookingStatus status,
            @RequestParam(required = false) String seatNumber) {
        bookingService.updateStatus(bookingId, status, seatNumber);
        return ResponseEntity.ok(
                "Booking status updated to " + status);
    }

    // GET /bookings/fare?flightId=1&passengers=2
    @GetMapping("/fare")
    public ResponseEntity<FareSummary> calculateFare(
            @RequestParam int flightId,
            @RequestParam int passengers) {
        FareSummary fare = bookingService
                .calculateFare(flightId, passengers);
        return ResponseEntity.ok(fare);
    }

    // POST /bookings/{bookingId}/addon
    @PostMapping("/{bookingId}/addon")
    public ResponseEntity<String> addAddOn(
            @PathVariable String bookingId,
            @RequestBody Map<String, Object> addOn) {
        String addOnType = (String) addOn.get("addOnType");
        double amount = ((Number) addOn.get("amount"))
                .doubleValue();
        bookingService.addAddOn(bookingId, addOnType, amount);
        return ResponseEntity.ok(
                "Add-on added successfully!");
    }

    // GET /bookings/upcoming/{userId}
    @GetMapping("/upcoming/{userId}")
    public ResponseEntity<List<Booking>> getUpcomingBookings(
            @PathVariable int userId) {
        List<Booking> bookings = bookingService
                .getUpcomingBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    // GET /bookings/all (Admin only)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService
                .getAllBookings();
        return ResponseEntity.ok(bookings);
    }
}