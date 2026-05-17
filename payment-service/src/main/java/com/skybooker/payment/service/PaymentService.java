package com.skybooker.payment.service;

import com.skybooker.payment.entity.Payment;
import com.skybooker.payment.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PaymentService {

    Payment initiatePayment(String bookingId, double amount, String paymentMode);

    Payment processPayment(Map<String, String> paymentData);

    Optional<Payment> getPaymentByBooking(String bookingId);

    List<Payment> getPaymentsByUser(int userId);

    Payment refundPayment(String paymentId);

    String getPaymentStatus(String paymentId);

    void updatePaymentStatus(String paymentId, String status);

    String generateReceipt(String paymentId);

    Double getRevenue(LocalDate startDate, LocalDate endDate);
}