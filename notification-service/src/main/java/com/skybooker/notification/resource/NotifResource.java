package com.skybooker.notification.resource;

import com.skybooker.notification.entity.Notification;
import com.skybooker.notification.enums.NotificationType;
import com.skybooker.notification.service.NotifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotifResource {

    @Autowired
    private NotifService notifService;

    // ── Get all notifications for a user ─────────────────────────
    @GetMapping("/recipient/{recipientId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<List<Notification>> getByRecipient(
            @PathVariable int recipientId) {

        List<Notification> notifications =
                notifService.getByRecipient(recipientId);
        return ResponseEntity.ok(notifications);
    }

    // ── Mark single notification as read ─────────────────────────
    @PutMapping("/read/{notificationId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<String> markAsRead(
            @PathVariable int notificationId) {

        notifService.markAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    // ── Mark all notifications as read for a user ─────────────────
    @PutMapping("/read-all/{recipientId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<String> markAllRead(
            @PathVariable int recipientId) {

        notifService.markAllRead(recipientId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    // ── Get unread count for a user ───────────────────────────────
    @GetMapping("/unread-count/{recipientId}")
    @PreAuthorize("hasAnyRole('PASSENGER','AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<Integer> getUnreadCount(
            @PathVariable int recipientId) {

        int count = notifService.getUnreadCount(recipientId);
        return ResponseEntity.ok(count);
    }

    // ── Delete a notification ─────────────────────────────────────
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<String> deleteNotification(
            @PathVariable int notificationId) {

        notifService.deleteNotification(notificationId);
        return ResponseEntity.ok("Notification deleted");
    }

    // ── Send booking confirmation ─────────────────────────────────
    @PostMapping("/booking-confirmation")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<String> sendBookingConfirmation(
            @RequestBody Map<String, String> request) {

        notifService.sendBookingConfirmation(
                request.get("bookingId"),
                Integer.parseInt(request.get("recipientId")),
                request.get("recipientEmail"),
                request.get("pnrCode")
        );
        return ResponseEntity.ok("Booking confirmation sent");
    }

    // ── Send bulk notification (Admin only) ───────────────────────
    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendBulk(
            @RequestBody Map<String, Object> request) {

        @SuppressWarnings("unchecked")
        List<Integer> recipientIds =
                (List<Integer>) request.get("recipientIds");
        String title   = (String) request.get("title");
        String message = (String) request.get("message");

        notifService.sendBulk(recipientIds, title, message);
        return ResponseEntity.ok("Bulk notification sent to "
                + recipientIds.size() + " recipients");
    }

    // ── Send flight alert ─────────────────────────────────────────
    @PostMapping("/flight-alert")
    @PreAuthorize("hasAnyRole('AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<String> sendFlightAlert(
            @RequestBody Map<String, String> request) {

        notifService.sendFlightAlert(
                Integer.parseInt(request.get("recipientId")),
                request.get("recipientEmail"),
                NotificationType.valueOf(request.get("type")),
                request.get("title"),
                request.get("message"),
                request.get("bookingId")
        );
        return ResponseEntity.ok("Flight alert sent");
    }

    // ── Send check-in reminder ────────────────────────────────────
    @PostMapping("/checkin-reminder")
    @PreAuthorize("hasAnyRole('PASSENGER','ADMIN')")
    public ResponseEntity<String> sendCheckinReminder(
            @RequestBody Map<String, String> request) {

        notifService.sendCheckinReminder(
                Integer.parseInt(request.get("recipientId")),
                request.get("recipientEmail"),
                request.get("bookingId"),
                request.get("flightDetails")
        );
        return ResponseEntity.ok("Check-in reminder sent");
    }

    // ── Send direct email ─────────────────────────────────────────
    @PostMapping("/send-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendEmail(
            @RequestBody Map<String, String> request) {

        notifService.sendEmail(
                request.get("toEmail"),
                request.get("subject"),
                request.get("body")
        );
        return ResponseEntity.ok("Email sent to " + request.get("toEmail"));
    }

    // ── Get all notifications (Admin only) ────────────────────────
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Notification>> getAll() {
        List<Notification> notifications = notifService.getAll();
        return ResponseEntity.ok(notifications);
    }

    // ── Send single notification ──────────────────────────────────
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('AIRLINE_STAFF','ADMIN')")
    public ResponseEntity<Notification> send(
            @RequestBody Notification notification) {

        Notification saved = notifService.send(notification);
        return ResponseEntity.ok(saved);
    }
}