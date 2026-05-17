package com.skybooker.flight.service;

import com.skybooker.flight.entity.Flight;
import com.skybooker.flight.entity.FlightStatus;
import com.skybooker.flight.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Override
    public Flight addFlight(Flight flight) {
        if (flightRepository.findByFlightNumber(flight.getFlightNumber()).isPresent()) {
            throw new RuntimeException("Flight number " + flight.getFlightNumber() + " already exists!");
        }

        if (flight.getStatus() == null) {
            flight.setStatus(FlightStatus.ON_TIME);
        }

        flight.setAvailableSeats(flight.getTotalSeats());
        return flightRepository.save(flight);
    }

    @Override
    public Optional<Flight> getFlightById(int flightId) {
        return flightRepository.findById(flightId);
    }

    @Override
    public Optional<Flight> getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    @Override
    public List<Flight> searchFlights(String origin,
                                      String destination,
                                      LocalDate date,
                                      int passengers) {

        return flightRepository.findByOriginAndDestAndDate(
                origin, destination, date, passengers);
    }

    @Override
    public Map<String, List<Flight>> searchRoundTrip(String origin,
                                                     String destination,
                                                     LocalDate departureDate,
                                                     LocalDate returnDate,
                                                     int passengers) {

        Map<String, List<Flight>> result = new HashMap<>();

        List<Flight> outbound = flightRepository
                .findByOriginAndDestAndDate(
                        origin, destination, departureDate, passengers);

        List<Flight> returnFlights = flightRepository
                .findByOriginAndDestAndDate(
                        destination, origin, returnDate, passengers);

        result.put("outbound", outbound);
        result.put("return", returnFlights);

        return result;
    }

    @Override
    public Flight updateFlight(int flightId, Flight updatedFlight) {
        Flight existing = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found!"));

        existing.setFlightNumber(updatedFlight.getFlightNumber());
        existing.setOriginAirportCode(updatedFlight.getOriginAirportCode());
        existing.setDestinationAirportCode(updatedFlight.getDestinationAirportCode());
        existing.setDepartureTime(updatedFlight.getDepartureTime());
        existing.setArrivalTime(updatedFlight.getArrivalTime());
        existing.setDurationMinutes(updatedFlight.getDurationMinutes());
        existing.setAircraftType(updatedFlight.getAircraftType());
        existing.setTotalSeats(updatedFlight.getTotalSeats());
        existing.setBasePrice(updatedFlight.getBasePrice());

        return flightRepository.save(existing);
    }

    @Override
    public void updateStatus(int flightId, FlightStatus status) {
        Flight existing = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found!"));

        existing.setStatus(status);
        flightRepository.save(existing);
    }

    @Override
    public void decrementSeats(int flightId, int count) {
        Flight existing = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found!"));

        if (existing.getAvailableSeats() < count) {
            throw new RuntimeException("Not enough seats available!");
        }

        existing.setAvailableSeats(existing.getAvailableSeats() - count);
        flightRepository.save(existing);
    }

    @Override
    public void incrementSeats(int flightId, int count) {
        Flight existing = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found!"));

        existing.setAvailableSeats(existing.getAvailableSeats() + count);
        flightRepository.save(existing);
    }

    @Override
    public void deleteFlight(int flightId) {
        Flight existing = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found!"));

        flightRepository.delete(existing);
    }

    @Override
    public List<Flight> getFlightsByAirline(int airlineId) {
        return flightRepository.findByAirlineId(airlineId);
    }

    @Override
    public List<Flight> findAvailableFlights() {
        return flightRepository.findAvailableFlights();
    }

    @Override
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
}