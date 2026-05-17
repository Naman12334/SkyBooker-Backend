package com.skybooker.flight.repository;

import com.skybooker.flight.entity.Flight;
import com.skybooker.flight.entity.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE f.originAirportCode = :origin " +
           "AND f.destinationAirportCode = :destination " +
           "AND DATE(f.departureTime) = :date " +
           "AND f.availableSeats >= :passengers " +
           "AND f.status != 'CANCELLED'")
    List<Flight> findByOriginAndDestAndDate(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("date") LocalDate date,
            @Param("passengers") int passengers);

    List<Flight> findByAirlineId(int airlineId);

    List<Flight> findByStatus(FlightStatus status);

    Optional<Flight> findByFlightId(int flightId);

    @Query("SELECT f FROM Flight f WHERE f.availableSeats > 0 " +
           "AND f.status != 'CANCELLED'")
    List<Flight> findAvailableFlights();

    long countByAirlineId(int airlineId);
}