package com.skybooker.payment.service;

import com.skybooker.payment.entity.Payment;
import com.skybooker.payment.enums.PaymentMode;
import com.skybooker.payment.enums.PaymentStatus;
import com.skybooker.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment initiatePayment(String bookingId, double amount, String paymentMode) {

        // Check if payment already exists for this booking
        Optional<Payment> existing = paymentRepository.findByBookingId(bookingId);
        if (existing.isPresent() &&
                existing.get().getStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Payment already completed for booking: "
                    + bookingId);
        }

        Payment payment = new Payment();
        payment.setBookingId(bookingId);
        payment.setAmount(amount);
        payment.setPaymentMode(PaymentMode.valueOf(paymentMode.toUpperCase()));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCurrency("INR");

        // Generate Razorpay Order ID simulation
        // In production this calls Razorpay API to create order
        String razorpayOrderId = "order_" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 16);
        payment.setRazorpayOrderId(razorpayOrderId);

        return paymentRepository.save(payment);
    }

    @Override
    public Payment processPayment(Map<String, String> paymentData) {

        String razorpayOrderId   = paymentData.get("razorpay_order_id");
        String razorpayPaymentId = paymentData.get("razorpay_payment_id");
        String razorpaySignature = paymentData.get("razorpay_signature");
        String bookingId         = paymentData.get("booking_id");
        String userId            = paymentData.get("user_id");

        // Find payment by order id
        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseGet(() -> {
                    // Create new payment record if not found
                    Payment p = new Payment();
                    p.setBookingId(bookingId);
                    p.setUserId(userId != null ? Integer.parseInt(userId) : 0);
                    p.setRazorpayOrderId(razorpayOrderId);
                    p.setCurrency("INR");
                    return p;
                });

        // Verify Razorpay signature
        // In production: use HMAC SHA256 to verify
        // razorpayOrderId + "|" + razorpayPaymentId with your Razorpay secret key
        boolean isValidSignature = verifyRazorpaySignature(
                razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (isValidSignature) {
            payment.setRazorpayPaymentId(razorpayPaymentId);
            payment.setRazorpaySignature(razorpaySignature);
            payment.setTransactionId(razorpayPaymentId);
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaidAt(LocalDateTime.now());
            payment.setGatewayResponse("Payment successful via Razorpay");
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setGatewayResponse("Signature verification failed");
        }

        return paymentRepository.save(payment);
    }

    // Razorpay signature verification
    // In production use HMAC SHA256
    private boolean verifyRazorpaySignature(String orderId,
                                             String paymentId,
                                             String signature) {
        // TODO: In production implement like this:
        // String data = orderId + "|" + paymentId;
        // String generatedSignature = hmacSHA256(data, RAZORPAY_KEY_SECRET);
        // return generatedSignature.equals(signature);

        // For test mode - accept if signature is not null
        return signature != null && !signature.isEmpty();
    }

    @Override
    public Optional<Payment> getPaymentByBooking(String bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Payment> getPaymentsByUser(int userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public Payment refundPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException(
                    "Cannot refund. Payment status is: " + payment.getStatus());
        }

        // In production: call Razorpay refund API here
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundAmount(payment.getAmount());
        payment.setRefundedAt(LocalDateTime.now());
        payment.setGatewayResponse("Refund processed successfully");

        return paymentRepository.save(payment);
    }

    @Override
    public String getPaymentStatus(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found: " + paymentId));
        return payment.getStatus().toString();
    }

    @Override
    public void updatePaymentStatus(String paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found: " + paymentId));
        payment.setStatus(PaymentStatus.valueOf(status.toUpperCase()));
        paymentRepository.save(payment);
    }

    @Override
    public String generateReceipt(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found: " + paymentId));

        // Returns receipt as formatted string
        // In production: generate PDF using iText7
        return String.format(
                "RECEIPT\n" +
                "=======\n" +
                "Payment ID   : %s\n" +
                "Booking ID   : %s\n" +
                "Amount       : INR %.2f\n" +
                "Mode         : %s\n" +
                "Status       : %s\n" +
                "Transaction  : %s\n" +
                "Date         : %s\n",
                payment.getPaymentId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getPaymentMode(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaidAt()
        );
    }

    @Override
    public Double getRevenue(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end   = endDate.atTime(23, 59, 59);
        Double revenue = paymentRepository.getTotalRevenue(start, end);
        return revenue != null ? revenue : 0.0;
    }
}