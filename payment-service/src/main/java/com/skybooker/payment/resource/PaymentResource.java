package com.skybooker.payment.resource;

import com.skybooker.payment.entity.Payment;
import com.skybooker.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentResource {

    @Autowired
    private PaymentService paymentService;

    // ── Initiate Payment ─────────────────────────────────────────
    // Called by booking-service or frontend after booking is created
    @PostMapping("/initiate")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<Payment> initiatePayment(
            @RequestParam String bookingId,
            @RequestParam double amount,
            @RequestParam String paymentMode) {

        Payment payment = paymentService.initiatePayment(
                bookingId, amount, paymentMode);
        return ResponseEntity.ok(payment);
    }

    // ── Process Payment (Razorpay callback) ──────────────────────
    // Called after Razorpay payment is done on frontend
    // Receives: razorpay_order_id, razorpay_payment_id,
    //           razorpay_signature, booking_id, user_id
    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(
            @RequestBody Map<String, String> paymentData) {

        Payment payment = paymentService.processPayment(paymentData);
        return ResponseEntity.ok(payment);
    }

    // ── Refund Payment ───────────────────────────────────────────
    @PostMapping("/refund/{paymentId}")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<Payment> refundPayment(
            @PathVariable String paymentId) {

        Payment payment = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(payment);
    }

    // ── Get Payment by Booking ID ────────────────────────────────
    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<?> getPaymentByBooking(
            @PathVariable String bookingId) {

        return paymentService.getPaymentByBooking(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Get All Payments by User ─────────────────────────────────
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<List<Payment>> getPaymentsByUser(
            @PathVariable int userId) {

        List<Payment> payments = paymentService.getPaymentsByUser(userId);
        return ResponseEntity.ok(payments);
    }

    // ── Get Payment Status ───────────────────────────────────────
    @GetMapping("/status/{paymentId}")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<String> getPaymentStatus(
            @PathVariable String paymentId) {

        String status = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(status);
    }

    // ── Generate Receipt ─────────────────────────────────────────
    @GetMapping("/receipt/{paymentId}")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<String> generateReceipt(
            @PathVariable String paymentId) {

        String receipt = paymentService.generateReceipt(paymentId);
        return ResponseEntity.ok(receipt);
    }

    // ── Get Revenue (Admin only) ─────────────────────────────────
    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        Double revenue = paymentService.getRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}