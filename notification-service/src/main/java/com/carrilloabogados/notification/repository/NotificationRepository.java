package com.carrilloabogados.notification.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrilloabogados.notification.domain.Notification;
import com.carrilloabogados.notification.domain.NotificationChannel;
import com.carrilloabogados.notification.domain.NotificationType;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    // Find by recipient
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    Page<Notification> findByRecipientId(UUID recipientId, Pageable pageable);

    // Find unread by recipient
    List<Notification> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(UUID recipientId);

    // Count unread
    long countByRecipientIdAndReadFalse(UUID recipientId);

    // Find by type
    List<Notification> findByRecipientIdAndNotificationTypeOrderByCreatedAtDesc(
            UUID recipientId, NotificationType notificationType);

    // Find by channel
    List<Notification> findByRecipientIdAndChannelOrderByCreatedAtDesc(
            UUID recipientId, NotificationChannel channel);

    // Mark as read
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.notificationId = :id")
    int markAsRead(@Param("id") UUID notificationId);

    // Mark all as read for user
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.recipientId = :recipientId AND n.read = false")
    int markAllAsReadForUser(@Param("recipientId") UUID recipientId);

    // Find recent notifications
    @Query("SELECT n FROM Notification n WHERE n.recipientId = :recipientId ORDER BY n.createdAt DESC")
    List<Notification> findRecentByRecipientId(@Param("recipientId") UUID recipientId, Pageable pageable);
}
