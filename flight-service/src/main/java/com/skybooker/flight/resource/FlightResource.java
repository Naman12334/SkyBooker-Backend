package com.skybooker.flight.resource;

import com.skybooker.flight.entity.Flight;
import com.skybooker.flight.entity.FlightStatus;
import com.skybooker.flight.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flights")
@CrossOrigin(origins = "*")
public class FlightResource {

    @Autowired
    private FlightService flightService;

    // GET /flights
    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    // POST /flights (Airline Staff or Admin only)
    @PostMapping
    @PreAuthorize("hasRole('AIRLINE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Flight> addFlight(
            @RequestBody Flight flight) {
        Flight created = flightService.addFlight(flight);
        return ResponseEntity.ok(created);
    }

    // GET /flights/{flightId}
    @GetMapping("/{flightId}")
    public ResponseEntity<Flight> getFlightById(
            @PathVariable int flightId) {
        return flightService.getFlightById(flightId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /flights/number/{flightNumber}
    @GetMapping("/number/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(
            @PathVariable String flightNumber) {
        return flightService.getFlightByNumber(flightNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /flights/search?origin=DEL&destination=BOM&date=2026-05-01&passengers=1
    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate date,
            @RequestParam(defaultValue = "1") int passengers) {
        List<Flight> flights = flightService.searchFlights(
                origin, destination, date, passengers);
        return ResponseEntity.ok(flights);
    }

    // GET /flights/search/roundtrip
    @GetMapping("/search/roundtrip")
    public ResponseEntity<Map<String, List<Flight>>> searchRoundTrip(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate departureDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate returnDate,
            @RequestParam(defaultValue = "1") int passengers) {
        Map<String, List<Flight>> flights = flightService
                .searchRoundTrip(origin, destination,
                        departureDate, returnDate, passengers);
        return ResponseEntity.ok(flights);
    }

    // PUT /flights/{flightId} (Airline Staff or Admin only)
    @PutMapping("/{flightId}")
    @PreAuthorize("hasRole('AIRLINE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Flight> updateFlight(
            @PathVariable int flightId,
            @RequestBody Flight flight) {
        Flight updated = flightService.updateFlight(flightId, flight);
        return ResponseEntity.ok(updated);
    }

    // PUT /flights/{flightId}/status (Airline Staff or Admin only)
    @PutMapping("/{flightId}/status")
    @PreAuthorize("hasRole('AIRLINE_STAFF') or hasRole('ADMIN')")
    public ResponseEntity<String> updateStatus(
            @PathVariable int flightId,
            @RequestParam FlightStatus status) {
        flightService.updateStatus(flightId, status);
        return ResponseEntity.ok(
                "Flight status updated to " + status);
    }

    // PUT /flights/{flightId}/decrement-seats
    @PutMapping("/{flightId}/decrement-seats")
    public ResponseEntity<String> decrementSeats(
            @PathVariable int flightId,
            @RequestParam int count) {
        flightService.decrementSeats(flightId, count);
        return ResponseEntity.ok("Seats decremented successfully!");
    }

    // PUT /flights/{flightId}/increment-seats
    @PutMapping("/{flightId}/increment-seats")
    public ResponseEntity<String> incrementSeats(
            @PathVariable int flightId,
            @RequestParam int count) {
        flightService.incrementSeats(flightId, count);
        return ResponseEntity.ok("Seats incremented successfully!");
    }

    // DELETE /flights/{flightId} (Admin only)
    @DeleteMapping("/{flightId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFlight(
            @PathVariable int flightId) {
        flightService.deleteFlight(flightId);
        return ResponseEntity.ok("Flight deleted successfully!");
    }

    // GET /flights/airline/{airlineId}
    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<Flight>> getFlightsByAirline(
            @PathVariable int airlineId) {
        List<Flight> flights = flightService
                .getFlightsByAirline(airlineId);
        return ResponseEntity.ok(flights);
    }

    // GET /flights/available
    @GetMapping("/available")
    public ResponseEntity<List<Flight>> findAvailableFlights() {
        List<Flight> flights = flightService.findAvailableFlights();
        return ResponseEntity.ok(flights);
    }
}