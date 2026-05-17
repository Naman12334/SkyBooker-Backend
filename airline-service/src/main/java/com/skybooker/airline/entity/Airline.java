package com.skybooker.airline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "airlines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int airlineId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String iataCode;

    private String icaoCode;

    private String logoUrl;

    private String country;

    private String contactEmail;

    private String contactPhone;

    @Column(nullable = false)
    private boolean isActive = true;
}