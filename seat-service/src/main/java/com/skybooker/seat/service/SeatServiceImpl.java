package com.skybooker.seat.service;

import com.skybooker.seat.entity.Seat;
import com.skybooker.seat.entity.SeatClass;
import com.skybooker.seat.entity.SeatStatus;
import com.skybooker.seat.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Override
    public List<Seat> addSeatsForFlight(int flightId,
            List<Seat> seats) {
        seats.forEach(seat -> {
            seat.setFlightId(flightId);
            seat.setStatus(SeatStatus.AVAILABLE);
        });
        return seatRepository.saveAll(seats);
    }

    @Override
    public List<Seat> getAvailableSeats(int flightId) {
        return seatRepository.findAvailableByFlightId(flightId);
    }

    @Override
    public List<Seat> getAvailableByClass(int flightId,
            SeatClass seatClass) {
        return seatRepository
                .findByFlightIdAndSeatClass(flightId, seatClass)
                .stream()
                .filter(seat -> seat.getStatus()
                        == SeatStatus.AVAILABLE)
                .toList();
    }

    @Override
    public Optional<Seat> getSeatById(int seatId) {
        return seatRepository.findById(seatId);
    }

    // ── Hold Seat with Optimistic Locking ─────────────────────────
    // If two users try to hold the same seat at the same time:
    // First user succeeds, second user gets clear error message
    @Override
    @Transactional
    public void holdSeat(int seatId, int userId) {
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException(
                            "Seat not found: " + seatId));

            // Check if seat is available
            if (seat.getStatus() != SeatStatus.AVAILABLE) {
                throw new RuntimeException(
                        "Seat is not available! Current status: "
                        + seat.getStatus());
            }

            // Check if hold has expired for previously held seat
            if (seat.getHoldExpiresAt() != null
                    && seat.getHoldExpiresAt()
                            .isBefore(LocalDateTime.now())) {
                // Hold expired — release it and allow new hold
                seat.setStatus(SeatStatus.AVAILABLE);
                seat.setHeldByUserId(null);
                seat.setHoldExpiresAt(null);
            }

            // Hold the seat for 15 minutes
            seat.setStatus(SeatStatus.HELD);
            seat.setHeldByUserId(userId);
            seat.setHoldExpiresAt(LocalDateTime.now().plusMinutes(15));

            // Save — if another user saved first,
            // ObjectOptimisticLockingFailureException is thrown
            seatRepository.save(seat);

        } catch (ObjectOptimisticLockingFailureException e) {
            // Another user grabbed this seat at the same time
            throw new RuntimeException(
                    "Seat " + seatId + " was just taken by another user. "
                    + "Please select a different seat.");
        }
    }

    // ── Release Seat ──────────────────────────────────────────────
    @Override
    @Transactional
    public void releaseSeat(int seatId) {
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException(
                            "Seat not found: " + seatId));

            if (seat.getStatus() != SeatStatus.HELD) {
                throw new RuntimeException(
                        "Seat is not held! Current status: "
                        + seat.getStatus());
            }

            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHeldByUserId(null);
            seat.setHoldExpiresAt(null);
            seatRepository.save(seat);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    "Could not release seat " + seatId
                    + ". Please try again.");
        }
    }

    // ── Confirm Seat ──────────────────────────────────────────────
    @Override
    @Transactional
    public void confirmSeat(int seatId) {
        try {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new RuntimeException(
                            "Seat not found: " + seatId));

            if (seat.getStatus() != SeatStatus.HELD) {
                throw new RuntimeException(
                        "Seat is not held! Current status: "
                        + seat.getStatus());
            }

            seat.setStatus(SeatStatus.CONFIRMED);
            seat.setHoldExpiresAt(null);
            seatRepository.save(seat);

        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException(
                    "Could not confirm seat " + seatId
                    + ". Please try again.");
        }
    }

    // ── Update Seat ───────────────────────────────────────────────
    @Override
    public Seat updateSeat(int seatId, Seat updatedSeat) {
        Seat existing = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException(
                        "Seat not found: " + seatId));
        existing.setSeatNumber(updatedSeat.getSeatNumber());
        existing.setSeatClass(updatedSeat.getSeatClass());
        existing.setRow(updatedSeat.getRow());
        existing.setColumn(updatedSeat.getColumn());
        existing.setWindow(updatedSeat.isWindow());
        existing.setAisle(updatedSeat.isAisle());
        existing.setHasExtraLegroom(updatedSeat.isHasExtraLegroom());
        existing.setPriceMultiplier(updatedSeat.getPriceMultiplier());
        return seatRepository.save(existing);
    }

    // ── Get Seat Map ──────────────────────────────────────────────
    @Override
    public List<Seat> getSeatMap(int flightId) {
        return seatRepository.findByFlightId(flightId);
    }

    // ── Count Available By Class ──────────────────────────────────
    @Override
    public int countAvailableByClass(int flightId,
            SeatClass seatClass) {
        return seatRepository.countAvailableByClass(
                flightId, seatClass);
    }

    // ── Delete Seats For Flight ───────────────────────────────────
    @Override
    @Transactional
    public void deleteSeatsForFlight(int flightId) {
        seatRepository.deleteByFlightId(flightId);
    }

 // ── Auto Release Expired Holds ────────────────────────────────
 // Called by SeatHoldScheduler every 2 minutes
 @Transactional
 public void releaseExpiredHolds() {
     LocalDateTime now = LocalDateTime.now();

     // Use optimized query to find only expired holds
     List<Seat> expiredSeats = seatRepository.findExpiredHolds(now);

     int released = 0;
     for (Seat seat : expiredSeats) {
         seat.setStatus(SeatStatus.AVAILABLE);
         seat.setHeldByUserId(null);
         seat.setHoldExpiresAt(null);
         seatRepository.save(seat);
         released++;
     }

     if (released > 0) {
         System.out.println("Released " + released
                 + " expired seat holds at: " + now);
     }
 }
}