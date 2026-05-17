package com.skybooker.airline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "airports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int airportId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String iataCode;

    private String icaoCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    private double latitude;

    private double longitude;

    private String timezone;
}