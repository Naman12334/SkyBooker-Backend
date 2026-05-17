package com.skybooker.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @Column(nullable = false, unique = true)
    private String bookingId;

    @Column(nullable = false)
    private int userId;

    @Column(nullable = false)
    private int flightId;

    // For round trip
    private Integer returnFlightId;

    @Column(nullable = false, unique = true)
    private String pnrCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TripType tripType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private double totalFare;

    @Column(nullable = false)
    private double baseFare;

    @Column(nullable = false)
    private double taxes;

    private String mealPreference;

    private double luggageKg;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String contactPhone;

    @Column(updatable = false)
    private LocalDateTime bookedAt;

    private String paymentId;

    private String seatNumber;

    @PrePersist
    protected void onCreate() {
        bookedAt = LocalDateTime.now();
    }
}