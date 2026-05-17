package com.skybooker.notification.service;

import com.skybooker.notification.entity.Notification;
import com.skybooker.notification.enums.NotificationChannel;
import com.skybooker.notification.enums.NotificationType;
import com.skybooker.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifServiceImpl implements NotifService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    // ── Send single notification ──────────────────────────────────
    @Override
    public Notification send(Notification notification) {
        Notification saved = notificationRepository.save(notification);

        // Send via correct channel
        if (notification.getChannel() == NotificationChannel.EMAIL
                && notification.getRecipientEmail() != null) {
            sendEmail(
                    notification.getRecipientEmail(),
                    notification.getTitle(),
                    notification.getMessage()
            );
        }

        return saved;
    }

    // ── Send booking confirmation ─────────────────────────────────
    @Override
    public void sendBookingConfirmation(String bookingId, int recipientId,
                                         String recipientEmail, String pnrCode) {

        String title   = "Booking Confirmed - PNR: " + pnrCode;
        String message = "Your booking has been confirmed!\n\n" +
                         "PNR Code  : " + pnrCode + "\n" +
                         "Booking ID: " + bookingId + "\n\n" +
                         "Thank you for choosing SkyBooker!\n" +
                         "Have a safe flight.";

        // Save in-app notification
        Notification appNotif = new Notification();
        appNotif.setRecipientId(recipientId);
        appNotif.setType(NotificationType.BOOKING_CONFIRMED);
        appNotif.setTitle(title);
        appNotif.setMessage(message);
        appNotif.setChannel(NotificationChannel.APP);
        appNotif.setRelatedBookingId(bookingId);
        notificationRepository.save(appNotif);

        // Send email notification
        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            Notification emailNotif = new Notification();
            emailNotif.setRecipientId(recipientId);
            emailNotif.setType(NotificationType.BOOKING_CONFIRMED);
            emailNotif.setTitle(title);
            emailNotif.setMessage(message);
            emailNotif.setChannel(NotificationChannel.EMAIL);
            emailNotif.setRelatedBookingId(bookingId);
            emailNotif.setRecipientEmail(recipientEmail);
            notificationRepository.save(emailNotif);

            sendEmail(recipientEmail, title, message);
        }
    }

    // ── Send bulk notifications ───────────────────────────────────
    @Override
    public void sendBulk(List<Integer> recipientIds,
                          String title, String message) {
        for (int recipientId : recipientIds) {
            Notification notification = new Notification();
            notification.setRecipientId(recipientId);
            notification.setType(NotificationType.BOOKING_CONFIRMED);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setChannel(NotificationChannel.APP);
            notificationRepository.save(notification);
        }
    }

    // ── Mark single notification as read ─────────────────────────
    @Override
    public void markAsRead(int notificationId) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new RuntimeException(
                        "Notification not found: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // ── Mark all notifications as read for a user ─────────────────
    @Override
    public void markAllRead(int recipientId) {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndIsRead(recipientId, false);
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    // ── Get all notifications for a user ─────────────────────────
    @Override
    public List<Notification> getByRecipient(int recipientId) {
        return notificationRepository.findByRecipientId(recipientId);
    }

    // ── Get unread count for a user ───────────────────────────────
    @Override
    public int getUnreadCount(int recipientId) {
        return notificationRepository
                .countByRecipientIdAndIsRead(recipientId, false);
    }

    // ── Delete a notification ─────────────────────────────────────
    @Override
    public void deleteNotification(int notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // ── Send email via JavaMailSender ─────────────────────────────
    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@skybooker.com");
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but do not crash the service
            System.err.println("Failed to send email to "
                    + toEmail + ": " + e.getMessage());
        }
    }

    // ── Send flight alert (delay/gate change/cancellation) ────────
    @Override
    public void sendFlightAlert(int recipientId, String recipientEmail,
                                 NotificationType type, String title,
                                 String message, String bookingId) {

        // Save in-app notification
        Notification appNotif = new Notification();
        appNotif.setRecipientId(recipientId);
        appNotif.setType(type);
        appNotif.setTitle(title);
        appNotif.setMessage(message);
        appNotif.setChannel(NotificationChannel.APP);
        appNotif.setRelatedBookingId(bookingId);
        notificationRepository.save(appNotif);

        // Send email if available
        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            Notification emailNotif = new Notification();
            emailNotif.setRecipientId(recipientId);
            emailNotif.setType(type);
            emailNotif.setTitle(title);
            emailNotif.setMessage(message);
            emailNotif.setChannel(NotificationChannel.EMAIL);
            emailNotif.setRelatedBookingId(bookingId);
            emailNotif.setRecipientEmail(recipientEmail);
            notificationRepository.save(emailNotif);

            sendEmail(recipientEmail, title, message);
        }
    }

    // ── Send check-in reminder ────────────────────────────────────
    @Override
    public void sendCheckinReminder(int recipientId, String recipientEmail,
                                     String bookingId, String flightDetails) {

        String title   = "Check-In Reminder - SkyBooker";
        String message = "Your flight departs in 24 hours!\n\n" +
                         "Flight Details: " + flightDetails + "\n\n" +
                         "Click here to complete web check-in.\n" +
                         "Check-in closes 1 hour before departure.\n\n" +
                         "Have a great flight!";

        // Save in-app notification
        Notification appNotif = new Notification();
        appNotif.setRecipientId(recipientId);
        appNotif.setType(NotificationType.CHECKIN_REMINDER);
        appNotif.setTitle(title);
        appNotif.setMessage(message);
        appNotif.setChannel(NotificationChannel.APP);
        appNotif.setRelatedBookingId(bookingId);
        notificationRepository.save(appNotif);

        // Send email
        if (recipientEmail != null && !recipientEmail.isEmpty()) {
            Notification emailNotif = new Notification();
            emailNotif.setRecipientId(recipientId);
            emailNotif.setType(NotificationType.CHECKIN_REMINDER);
            emailNotif.setTitle(title);
            emailNotif.setMessage(message);
            emailNotif.setChannel(NotificationChannel.EMAIL);
            emailNotif.setRelatedBookingId(bookingId);
            emailNotif.setRecipientEmail(recipientEmail);
            notificationRepository.save(emailNotif);

            sendEmail(recipientEmail, title, message);
        }
    }

    // ── Get all notifications (Admin) ─────────────────────────────
    @Override
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }
}