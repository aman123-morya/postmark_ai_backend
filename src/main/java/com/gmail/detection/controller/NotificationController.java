package com.gmail.detection.controller;

import com.gmail.detection.dto.CreateNotificationRequest;
import com.gmail.detection.dto.NotificationDTO;
import com.gmail.detection.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Personal + broadcast in-app notifications. All endpoints require an
 * authenticated user (default rule in SecurityConfig); creating a
 * notification for someone else is further restricted to ADMIN below.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMine(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getForUser(authentication.getName()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(Authentication authentication) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.unreadCount(authentication.getName())));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markRead(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(notificationService.markRead(id, authentication.getName()));
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllRead(Authentication authentication) {
        notificationService.markAllRead(authentication.getName());
        return ResponseEntity.ok("All notifications marked as read.");
    }

    // Admin-only: broadcast an announcement or notify a specific user.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationDTO> create(@Valid @RequestBody CreateNotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.create(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok("Notification deleted successfully.");
    }
}
