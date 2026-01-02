package com.carrilloabogados.notification.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carrilloabogados.notification.domain.Notification;
import com.carrilloabogados.notification.domain.NotificationStatus;
import com.carrilloabogados.notification.dto.CreateNotificationRequest;
import com.carrilloabogados.notification.dto.NotificationDto;
import com.carrilloabogados.notification.repository.NotificationRepository;

@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationDto createNotification(CreateNotificationRequest request) {
        log.info("Creating notification for recipient: {}", request.getRecipientId());

        Notification notification = new Notification();
        notification.setRecipientId(request.getRecipientId());
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setNotificationType(request.getNotificationType());
        notification.setChannel(request.getChannel());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setRelatedEntityType(request.getRelatedEntityType());
        notification.setRelatedEntityId(request.getRelatedEntityId());
        notification.setActionUrl(request.getActionUrl());
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);

        // For IN_APP notifications, mark as sent immediately
        if (request.getChannel() == com.carrilloabogados.notification.domain.NotificationChannel.IN_APP) {
            saved.setStatus(NotificationStatus.SENT);
            saved.setSentAt(Instant.now());
            saved = notificationRepository.save(saved);
        }

        log.info("Notification created with ID: {}", saved.getNotificationId());
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsForUser(UUID recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotificationsForUserPaginated(UUID recipientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return notificationRepository.findByRecipientId(recipientId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(UUID recipientId) {
        return notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(recipientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getRecentNotifications(UUID recipientId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return notificationRepository.findRecentByRecipientId(recipientId, pageable)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<NotificationDto> getNotificationById(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .map(this::toDto);
    }

    public boolean markAsRead(UUID notificationId) {
        int updated = notificationRepository.markAsRead(notificationId);
        if (updated > 0) {
            log.info("Notification {} marked as read", notificationId);
            return true;
        }
        return false;
    }

    public int markAllAsRead(UUID recipientId) {
        int updated = notificationRepository.markAllAsReadForUser(recipientId);
        log.info("Marked {} notifications as read for user {}", updated, recipientId);
        return updated;
    }

    public boolean deleteNotification(UUID notificationId) {
        if (notificationRepository.existsById(notificationId)) {
            notificationRepository.deleteById(notificationId);
            log.info("Notification {} deleted", notificationId);
            return true;
        }
        return false;
    }

    private NotificationDto toDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setRecipientId(notification.getRecipientId());
        dto.setRecipientEmail(notification.getRecipientEmail());
        dto.setNotificationType(notification.getNotificationType());
        dto.setChannel(notification.getChannel());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRelatedEntityType(notification.getRelatedEntityType());
        dto.setRelatedEntityId(notification.getRelatedEntityId());
        dto.setActionUrl(notification.getActionUrl());
        dto.setRead(notification.isRead());
        dto.setReadAt(notification.getReadAt());
        dto.setStatus(notification.getStatus());
        dto.setSentAt(notification.getSentAt());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
