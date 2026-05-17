package com.skybooker.seat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seatId;

    @Column(nullable = false)
    private int flightId;

    @Column(nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatClass seatClass;

    @Column(name = "seat_row", nullable = false)
    private int row;

    @Column(name = "seat_column", nullable = false)
    private String column;

    @Column(nullable = false)
    private boolean isWindow;

    @Column(nullable = false)
    private boolean isAisle;

    @Column(nullable = false)
    private boolean hasExtraLegroom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Column(nullable = false)
    private double priceMultiplier;

    // ── Optimistic Locking ────────────────────────────────────────
    // This field is automatically incremented by JPA every time
    // the seat record is updated.
    // If two users try to update the same seat at the same time:
    // - First user succeeds
    // - Second user gets OptimisticLockException
    // This prevents double booking of the same seat!
    @Version
    @Column(name = "version")
    private int version;

    // ── Seat Hold Expiry ──────────────────────────────────────────
    // When seat is HELD, this stores when the hold expires
    // After 15 minutes if not paid, seat auto-releases
    @Column(name = "hold_expires_at")
    private LocalDateTime holdExpiresAt;

    // ── Held By ───────────────────────────────────────────────────
    // Stores which user is currently holding this seat
    @Column(name = "held_by_user_id")
    private Integer heldByUserId;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = SeatStatus.AVAILABLE;
        }
        if (this.priceMultiplier == 0) {
            this.priceMultiplier = 1.0;
        }
    }
}