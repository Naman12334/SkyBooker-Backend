package com.skybooker.passenger.resource;

import com.skybooker.passenger.entity.PassengerInfo;
import com.skybooker.passenger.service.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/passengers")
public class PassengerResource {

    @Autowired
    private PassengerService passengerService;

    // ── Add Passenger ─────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<PassengerInfo> addPassenger(
            @RequestBody PassengerInfo passengerInfo) {

        passengerService.validatePassengerData(passengerInfo);
        PassengerInfo saved = passengerService.addPassenger(passengerInfo);
        return ResponseEntity.ok(saved);
    }

    // ── Get Passenger By ID ───────────────────────────────────────
    @GetMapping("/{passengerId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<?> getPassengerById(
            @PathVariable int passengerId) {

        return passengerService.getPassengerById(passengerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Get Passengers By Booking ─────────────────────────────────
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<List<PassengerInfo>> getPassengersByBooking(
            @PathVariable String bookingId) {

        List<PassengerInfo> passengers =
                passengerService.getPassengersByBooking(bookingId);
        return ResponseEntity.ok(passengers);
    }

    // ── Get By Passport Number ────────────────────────────────────
    @GetMapping("/passport/{passportNumber}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<?> getByPassportNumber(
            @PathVariable String passportNumber) {

        return passengerService.getByPassportNumber(passportNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Update Passenger ──────────────────────────────────────────
    @PutMapping("/{passengerId}")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<PassengerInfo> updatePassenger(
            @PathVariable int passengerId,
            @RequestBody PassengerInfo passengerInfo) {

        PassengerInfo updated = passengerService.updatePassenger(
                passengerId, passengerInfo);
        return ResponseEntity.ok(updated);
    }

    // ── Assign Seat ───────────────────────────────────────────────
    @PutMapping("/{passengerId}/assign-seat")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<String> assignSeat(
            @PathVariable int passengerId,
            @RequestParam String seatNumber) {

        passengerService.assignSeat(passengerId, seatNumber);
        return ResponseEntity.ok("Seat " + seatNumber
                + " assigned to passenger " + passengerId);
    }

    // ── Delete Passenger ──────────────────────────────────────────
    @DeleteMapping("/{passengerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePassenger(
            @PathVariable int passengerId) {

        passengerService.deletePassenger(passengerId);
        return ResponseEntity.ok("Passenger deleted: " + passengerId);
    }

    // ── Get Passenger Count ───────────────────────────────────────
    @GetMapping("/count/{bookingId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<Integer> getPassengerCount(
            @PathVariable String bookingId) {

        int count = passengerService.getPassengerCount(bookingId);
        return ResponseEntity.ok(count);
    }

    // ─────────────────────────────────────────────────────────────
    // WEB CHECK-IN ENDPOINTS
    // ─────────────────────────────────────────────────────────────

    // ── Perform Web Check-In ──────────────────────────────────────
    // POST /passengers/checkin
    // Body: { passengerId, bookingId, departureDateTime }
    // departureDateTime format: "yyyy-MM-dd HH:mm:ss"
    @PostMapping("/checkin")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<PassengerInfo> webCheckIn(
            @RequestBody Map<String, String> request) {

        int passengerId = Integer.parseInt(request.get("passengerId"));
        String bookingId = request.get("bookingId");
        String departureDateTime = request.get("departureDateTime");

        PassengerInfo checkedIn = passengerService.webCheckIn(
                passengerId, bookingId, departureDateTime);

        return ResponseEntity.ok(checkedIn);
    }

    // ── Get Check-In Status ───────────────────────────────────────
    @GetMapping("/checkin/status/{passengerId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<Boolean> getCheckInStatus(
            @PathVariable int passengerId) {

        boolean status = passengerService.getCheckInStatus(passengerId);
        return ResponseEntity.ok(status);
    }

    // ── Get All Checked-In Passengers for a Booking ───────────────
    @GetMapping("/checkin/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<List<PassengerInfo>> getCheckedInPassengers(
            @PathVariable String bookingId) {

        List<PassengerInfo> passengers =
                passengerService.getCheckedInPassengers(bookingId);
        return ResponseEntity.ok(passengers);
    }

    // ── Get Checked-In Count ──────────────────────────────────────
    @GetMapping("/checkin/count/{bookingId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<Integer> getCheckedInCount(
            @PathVariable String bookingId) {

        int count = passengerService.getCheckedInCount(bookingId);
        return ResponseEntity.ok(count);
    }

    // ── Generate Boarding Pass ────────────────────────────────────
    @GetMapping("/boarding-pass/{passengerId}")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<String> generateBoardingPass(
            @PathVariable int passengerId) {

        String boardingPass =
                passengerService.generateBoardingPass(passengerId);
        return ResponseEntity.ok(boardingPass);
    }
}