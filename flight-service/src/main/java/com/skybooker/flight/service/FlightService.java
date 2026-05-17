package com.skybooker.flight.service;

import com.skybooker.flight.entity.Flight;
import com.skybooker.flight.entity.FlightStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlightService {

    // Add new flight
    Flight addFlight(Flight flight);

    // Get flight by ID
    Optional<Flight> getFlightById(int flightId);

    // Get flight by flight number
    Optional<Flight> getFlightByNumber(String flightNumber);

    // Search one way flights
    List<Flight> searchFlights(String origin, String destination,
                               LocalDate date, int passengers);

    // Search round trip flights
    Map<String, List<Flight>> searchRoundTrip(String origin,
                                              String destination,
                                              LocalDate departureDate,
                                              LocalDate returnDate,
                                              int passengers);

    // Update flight
    Flight updateFlight(int flightId, Flight flight);

    // Update flight status
    void updateStatus(int flightId, FlightStatus status);

    // Decrement available seats
    void decrementSeats(int flightId, int count);

    // Increment available seats
    void incrementSeats(int flightId, int count);

    // Delete flight
    void deleteFlight(int flightId);

    // Get flights by airline
    List<Flight> getFlightsByAirline(int airlineId);

    // Get all available flights
    List<Flight> findAvailableFlights();

    List<Flight> getAllFlights();
}