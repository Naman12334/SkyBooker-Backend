package com.skybooker.airline.resource;

import com.skybooker.airline.entity.Airline;
import com.skybooker.airline.entity.Airport;
import com.skybooker.airline.repository.AirportRepository;
import com.skybooker.airline.service.AirlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AirlineResource {

    @Autowired
    private AirlineService airlineService;

    @Autowired
    private AirportRepository airportRepository;

    // ==================== AIRLINE ENDPOINTS ====================

    // POST /airlines
    @PostMapping("/airlines")
    public ResponseEntity<Airline> createAirline(@RequestBody Airline airline) {
        Airline created = airlineService.createAirline(airline);
        return ResponseEntity.ok(created);
    }

    // GET /airlines
    @GetMapping("/airlines")
    public ResponseEntity<List<Airline>> getAllAirlines() {
        List<Airline> airlines = airlineService.getAllAirlines();
        return ResponseEntity.ok(airlines);
    }

    // GET /airlines/{airlineId}
    @GetMapping("/airlines/{airlineId}")
    public ResponseEntity<Airline> getAirlineById(
            @PathVariable int airlineId) {
        return airlineService.getAirlineById(airlineId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /airlines/iata/{iataCode}
    @GetMapping("/airlines/iata/{iataCode}")
    public ResponseEntity<Airline> getAirlineByIata(
            @PathVariable String iataCode) {
        return airlineService.getAirlineByIata(iataCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /airlines/{airlineId}
    @PutMapping("/airlines/{airlineId}")
    public ResponseEntity<Airline> updateAirline(
            @PathVariable int airlineId,
            @RequestBody Airline airline) {
        Airline updated = airlineService.updateAirline(airlineId, airline);
        return ResponseEntity.ok(updated);
    }

    // PUT /airlines/{airlineId}/deactivate
    @PutMapping("/airlines/{airlineId}/deactivate")
    public ResponseEntity<String> deactivateAirline(
            @PathVariable int airlineId) {
        airlineService.deactivateAirline(airlineId);
        return ResponseEntity.ok("Airline deactivated successfully!");
    }

    // ==================== AIRPORT ENDPOINTS ====================

    // POST /airports
    @PostMapping("/airports")
    public ResponseEntity<Airport> createAirport(
            @RequestBody Airport airport) {
        Airport created = airlineService.createAirport(airport);
        return ResponseEntity.ok(created);
    }

    // GET /airports
    @GetMapping("/airports")
    public ResponseEntity<List<Airport>> getAllAirports() {
        List<Airport> airports = airlineService.getAllAirports();
        return ResponseEntity.ok(airports);
    }

    // GET /airports/iata/{iataCode}
    @GetMapping("/airports/iata/{iataCode}")
    public ResponseEntity<Airport> getAirportByIata(
            @PathVariable String iataCode) {
        return airlineService.getAirportByIata(iataCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /airports/search?keyword=delhi
    @GetMapping("/airports/search")
    public ResponseEntity<List<Airport>> searchAirports(
            @RequestParam String keyword) {
        List<Airport> airports = airlineService.searchAirports(keyword);
        return ResponseEntity.ok(airports);
    }

    // GET /airports/city/{city}
    @GetMapping("/airports/city/{city}")
    public ResponseEntity<List<Airport>> getAirportsByCity(
            @PathVariable String city) {
        List<Airport> airports = airlineService.getAirportsByCity(city);
        return ResponseEntity.ok(airports);
    }

    // GET /airports/country/{country}
    @GetMapping("/airports/country/{country}")
    public ResponseEntity<List<Airport>> getAirportsByCountry(
            @PathVariable String country) {
        List<Airport> airports = airportRepository.findByCountry(country);
        return ResponseEntity.ok(airports);
    }

    // PUT /airports/{airportId}
    @PutMapping("/airports/{airportId}")
    public ResponseEntity<Airport> updateAirport(
            @PathVariable int airportId,
            @RequestBody Airport airport) {
        Airport updated = airlineService.updateAirport(airportId, airport);
        return ResponseEntity.ok(updated);
    }
}