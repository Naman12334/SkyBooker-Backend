package com.skybooker.passenger.entity;

import com.skybooker.passenger.enums.PassengerType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "passenger_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "passenger_id")
    private int passengerId;

    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @Column(name = "title")
    private String title;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "passport_expiry")
    private LocalDate passportExpiry;

    @Column(name = "seat_id")
    private int seatId;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "ticket_number")
    private String ticketNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "passenger_type")
    private PassengerType passengerType;

    // ── Web Check-In Fields ───────────────────────────────────────
    @Column(name = "is_checked_in")
    private boolean isCheckedIn = false;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "boarding_pass_generated")
    private boolean boardingPassGenerated = false;

    @Column(name = "meal_preference")
    private String mealPreference;

    @PrePersist
    public void prePersist() {
        this.isCheckedIn = false;
        this.boardingPassGenerated = false;
    }
}