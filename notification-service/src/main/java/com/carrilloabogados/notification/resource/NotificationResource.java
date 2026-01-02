package com.carrilloabogados.notification.resource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrilloabogados.notification.dto.CreateNotificationRequest;
import com.carrilloabogados.notification.dto.DtoCollectionResponse;
import com.carrilloabogados.notification.dto.NotificationDto;
import com.carrilloabogados.notification.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationResource {

    private static final Logger log = LoggerFactory.getLogger(NotificationResource.class);

    private final NotificationService notificationService;

    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        log.info("POST /api/notifications - Creating notification for recipient: {}", request.getRecipientId());
        NotificationDto created = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<DtoCollectionResponse<NotificationDto>> getNotificationsForUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/notifications/user/{} - page: {}, size: {}", userId, page, size);

        if (size > 0) {
            Page<NotificationDto> notifications = notificationService.getNotificationsForUserPaginated(userId, page,
                    size);
            return ResponseEntity.ok(new DtoCollectionResponse<>(
                    notifications.getContent(),
                    notifications.getTotalElements(),
                    notifications.getNumber(),
                    notifications.getSize()));
        } else {
            List<NotificationDto> notifications = notificationService.getNotificationsForUser(userId);
            return ResponseEntity.ok(new DtoCollectionResponse<>(notifications));
        }
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<DtoCollectionResponse<NotificationDto>> getUnreadNotifications(
            @PathVariable UUID userId) {
        log.info("GET /api/notifications/user/{}/unread", userId);
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(new DtoCollectionResponse<>(notifications));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable UUID userId) {
        log.info("GET /api/notifications/user/{}/count", userId);
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<DtoCollectionResponse<NotificationDto>> getRecentNotifications(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "5") int limit) {
        log.info("GET /api/notifications/user/{}/recent - limit: {}", userId, limit);
        List<NotificationDto> notifications = notificationService.getRecentNotifications(userId, limit);
        return ResponseEntity.ok(new DtoCollectionResponse<>(notifications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationDto> getNotificationById(@PathVariable UUID id) {
        log.info("GET /api/notifications/{}", id);
        return notificationService.getNotificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Boolean>> markAsRead(@PathVariable UUID id) {
        log.info("PATCH /api/notifications/{}/read", id);
        boolean success = notificationService.markAsRead(id);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@PathVariable UUID userId) {
        log.info("PATCH /api/notifications/user/{}/read-all", userId);
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("success", true, "count", count));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        log.info("DELETE /api/notifications/{}", id);
        boolean success = notificationService.deleteNotification(id);
        if (success) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
