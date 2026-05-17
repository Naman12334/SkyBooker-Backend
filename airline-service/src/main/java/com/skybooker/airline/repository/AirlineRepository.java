package com.skybooker.airline.repository;

import com.skybooker.airline.entity.Airline;
import com.skybooker.airline.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Integer> {

    // Airline queries
    Optional<Airline> findByAirlineId(int airlineId);

    Optional<Airline> findByIataCode(String iataCode);

    List<Airline> findByIsActive(boolean isActive);

    // Airport queries using separate repo methods
    @Query("SELECT a FROM Airport a WHERE a.iataCode = :iataCode")
    Optional<Airport> findAirportByIataCode(String iataCode);

    @Query("SELECT a FROM Airport a WHERE a.city = :city")
    List<Airport> findAirportsByCity(String city);

    @Query("SELECT a FROM Airport a WHERE a.country = :country")
    List<Airport> findAirportsByCountry(String country);

    @Query("SELECT a FROM Airport a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.city) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(a.iataCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Airport> searchAirports(String keyword);
}