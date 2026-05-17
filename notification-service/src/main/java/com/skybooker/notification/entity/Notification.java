package com.skybooker.notification.entity;

import com.skybooker.notification.enums.NotificationChannel;
import com.skybooker.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;

    @Column(name = "recipient_id", nullable = false)
    private int recipientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private NotificationChannel channel;

    @Column(name = "related_booking_id")
    private String relatedBookingId;

    @Column(name = "is_read")
    private boolean isRead = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
        this.isRead = false;
    }
}