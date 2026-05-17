package com.skybooker.payment.repository;

import com.skybooker.payment.entity.Payment;
import com.skybooker.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByBookingId(String bookingId);

    List<Payment> findByUserId(int userId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.userId = :userId AND p.status = 'PAID'")
    Double sumAmountByUserId(@Param("userId") int userId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.status = 'PAID' AND p.paidAt BETWEEN :start AND :end")
    Double getTotalRevenue(@Param("start") LocalDateTime start,
                           @Param("end") LocalDateTime end);

    List<Payment> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status,
                                                  LocalDateTime dateTime);
}