package com.skybooker.airline.service;

import com.skybooker.airline.entity.Airline;
import com.skybooker.airline.entity.Airport;
import java.util.List;
import java.util.Optional;

public interface AirlineService {

    // Airline operations
    Airline createAirline(Airline airline);

    Optional<Airline> getAirlineById(int airlineId);

    Optional<Airline> getAirlineByIata(String iataCode);

    List<Airline> getAllAirlines();

    Airline updateAirline(int airlineId, Airline airline);

    void deactivateAirline(int airlineId);

    // Airport operations
    Airport createAirport(Airport airport);

    Optional<Airport> getAirportByIata(String iataCode);

    List<Airport> searchAirports(String keyword);

    List<Airport> getAirportsByCity(String city);

    Airport updateAirport(int airportId, Airport airport);

    List<Airport> getAllAirports();
}