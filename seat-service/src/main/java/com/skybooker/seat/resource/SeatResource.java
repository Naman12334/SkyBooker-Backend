package com.skybooker.seat.resource;

import com.skybooker.seat.entity.Seat;
import com.skybooker.seat.entity.SeatClass;
import com.skybooker.seat.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seats")
@CrossOrigin(origins = "*")
public class SeatResource {

    @Autowired
    private SeatService seatService;

    // ── Add Seats for Flight ──────────────────────────────────────
    @PostMapping("/{flightId}")
    @PreAuthorize("hasRole('AIRLINE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Seat>> addSeatsForFlight(
            @PathVariable int flightId,
            @RequestBody List<Seat> seats) {

        List<Seat> created = seatService.addSeatsForFlight(
                flightId, seats);
        return ResponseEntity.ok(created);
    }

    // ── Get Available Seats ───────────────────────────────────────
    @GetMapping("/available/{flightId}")
    public ResponseEntity<List<Seat>> getAvailableSeats(
            @PathVariable int flightId) {

        List<Seat> seats = seatService.getAvailableSeats(flightId);
        return ResponseEntity.ok(seats);
    }

    // ── Get Available Seats By Class ──────────────────────────────
    @GetMapping("/class/{flightId}/{seatClass}")
    public ResponseEntity<List<Seat>> getAvailableByClass(
            @PathVariable int flightId,
            @PathVariable SeatClass seatClass) {

        List<Seat> seats = seatService.getAvailableByClass(
                flightId, seatClass);
        return ResponseEntity.ok(seats);
    }

    // ── Get Seat By ID ────────────────────────────────────────────
    @GetMapping("/{seatId}")
    public ResponseEntity<Seat> getSeatById(
            @PathVariable int seatId) {

        return seatService.getSeatById(seatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Get Seat Map ──────────────────────────────────────────────
    @GetMapping("/map/{flightId}")
    public ResponseEntity<List<Seat>> getSeatMap(
            @PathVariable int flightId) {

        List<Seat> seats = seatService.getSeatMap(flightId);
        return ResponseEntity.ok(seats);
    }

    // ── Hold Seat with Optimistic Locking ─────────────────────────
    // Now requires userId to track who is holding the seat
    // userId passed as request param
    // Gateway forwards X-User-Id header automatically
    // Two users trying to hold same seat simultaneously:
    // First succeeds, second gets clear error message
    @PutMapping("/hold/{seatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> holdSeat(
            @PathVariable int seatId,
            @RequestParam int userId) {

        seatService.holdSeat(seatId, userId);
        return ResponseEntity.ok(
                "Seat " + seatId + " held successfully for 15 minutes!");
    }

    // ── Release Seat ──────────────────────────────────────────────
    @PutMapping("/release/{seatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> releaseSeat(
            @PathVariable int seatId) {

        seatService.releaseSeat(seatId);
        return ResponseEntity.ok(
                "Seat " + seatId + " released successfully!");
    }

    // ── Confirm Seat ──────────────────────────────────────────────
    @PutMapping("/confirm/{seatId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> confirmSeat(
            @PathVariable int seatId) {

        seatService.confirmSeat(seatId);
        return ResponseEntity.ok(
                "Seat " + seatId + " confirmed successfully!");
    }

    // ── Update Seat ───────────────────────────────────────────────
    @PutMapping("/{seatId}")
    @PreAuthorize("hasRole('AIRLINE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Seat> updateSeat(
            @PathVariable int seatId,
            @RequestBody Seat seat) {

        Seat updated = seatService.updateSeat(seatId, seat);
        return ResponseEntity.ok(updated);
    }

    // ── Count Available By Class ──────────────────────────────────
    @GetMapping("/count/{flightId}/{seatClass}")
    public ResponseEntity<Integer> countAvailableByClass(
            @PathVariable int flightId,
            @PathVariable SeatClass seatClass) {

        int count = seatService.countAvailableByClass(
                flightId, seatClass);
        return ResponseEntity.ok(count);
    }

    // ── Delete All Seats For Flight ───────────────────────────────
    @DeleteMapping("/flight/{flightId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteSeatsForFlight(
            @PathVariable int flightId) {

        seatService.deleteSeatsForFlight(flightId);
        return ResponseEntity.ok(
                "All seats deleted for flight " + flightId);
    }
}