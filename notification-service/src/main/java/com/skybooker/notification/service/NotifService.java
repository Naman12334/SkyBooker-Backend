package com.skybooker.notification.service;

import com.skybooker.notification.entity.Notification;
import com.skybooker.notification.enums.NotificationChannel;
import com.skybooker.notification.enums.NotificationType;

import java.util.List;

public interface NotifService {

    Notification send(Notification notification);

    void sendBookingConfirmation(String bookingId, int recipientId,
                                  String recipientEmail, String pnrCode);

    void sendBulk(List<Integer> recipientIds, String title, String message);

    void markAsRead(int notificationId);

    void markAllRead(int recipientId);

    List<Notification> getByRecipient(int recipientId);

    int getUnreadCount(int recipientId);

    void deleteNotification(int notificationId);

    void sendEmail(String toEmail, String subject, String body);

    void sendFlightAlert(int recipientId, String recipientEmail,
                          NotificationType type, String title,
                          String message, String bookingId);

    void sendCheckinReminder(int recipientId, String recipientEmail,
                              String bookingId, String flightDetails);

    List<Notification> getAll();
}