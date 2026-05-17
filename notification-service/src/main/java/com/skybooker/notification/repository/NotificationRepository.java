package com.skybooker.notification.repository;

import com.skybooker.notification.entity.Notification;
import com.skybooker.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByRecipientId(int recipientId);

    List<Notification> findByRecipientIdAndIsRead(int recipientId, boolean isRead);

    int countByRecipientIdAndIsRead(int recipientId, boolean isRead);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByRelatedBookingId(String relatedBookingId);

    void deleteByNotificationId(int notificationId);
}