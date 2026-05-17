package com.skybooker.airline.service;

import com.skybooker.airline.entity.Airline;
import com.skybooker.airline.entity.Airport;
import com.skybooker.airline.repository.AirlineRepository;
import com.skybooker.airline.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirlineServiceImpl implements AirlineService {

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private AirportRepository airportRepository;

    // ==================== AIRLINE OPERATIONS ====================

    @Override
    public Airline createAirline(Airline airline) {
        if (airlineRepository.findByIataCode(airline.getIataCode()).isPresent()) {
            throw new RuntimeException("Airline with IATA code "
                    + airline.getIataCode() + " already exists!");
        }
        airline.setActive(true);
        return airlineRepository.save(airline);
    }

    @Override
    public Optional<Airline> getAirlineById(int airlineId) {
        return airlineRepository.findById(airlineId);
    }

    @Override
    public Optional<Airline> getAirlineByIata(String iataCode) {
        return airlineRepository.findByIataCode(iataCode);
    }

    @Override
    public List<Airline> getAllAirlines() {
        return airlineRepository.findAll();
    }

    @Override
    public Airline updateAirline(int airlineId, Airline updatedAirline) {
        Airline existing = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline not found!"));
        existing.setName(updatedAirline.getName());
        existing.setLogoUrl(updatedAirline.getLogoUrl());
        existing.setCountry(updatedAirline.getCountry());
        existing.setContactEmail(updatedAirline.getContactEmail());
        existing.setContactPhone(updatedAirline.getContactPhone());
        existing.setIcaoCode(updatedAirline.getIcaoCode());
        return airlineRepository.save(existing);
    }

    @Override
    public void deactivateAirline(int airlineId) {
        Airline existing = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline not found!"));
        existing.setActive(false);
        airlineRepository.save(existing);
    }

    // ==================== AIRPORT OPERATIONS ====================

    @Override
    public Airport createAirport(Airport airport) {
        if (airportRepository.findByIataCode(airport.getIataCode()).isPresent()) {
            throw new RuntimeException("Airport with IATA code "
                    + airport.getIataCode() + " already exists!");
        }
        return airportRepository.save(airport);
    }

    @Override
    public Optional<Airport> getAirportByIata(String iataCode) {
        return airportRepository.findByIataCode(iataCode);
    }

    @Override
    public List<Airport> searchAirports(String keyword) {
        return airportRepository.searchAirports(keyword);
    }

    @Override
    public List<Airport> getAirportsByCity(String city) {
        return airportRepository.findByCity(city);
    }

    @Override
    public Airport updateAirport(int airportId, Airport updatedAirport) {
        Airport existing = airportRepository.findById(airportId)
                .orElseThrow(() -> new RuntimeException("Airport not found!"));
        existing.setName(updatedAirport.getName());
        existing.setCity(updatedAirport.getCity());
        existing.setCountry(updatedAirport.getCountry());
        existing.setLatitude(updatedAirport.getLatitude());
        existing.setLongitude(updatedAirport.getLongitude());
        existing.setTimezone(updatedAirport.getTimezone());
        existing.setIcaoCode(updatedAirport.getIcaoCode());
        return airportRepository.save(existing);
    }

    @Override
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }
}