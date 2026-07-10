package com.gmail.detection.entity;

import com.gmail.detection.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

/**
 * In-app notification. recipientEmail == null means it's a broadcast that
 * every authenticated user should see (e.g. an admin announcement).
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    // null = broadcast to all users
    private String recipientEmail;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // Note: for broadcast notifications (recipientEmail == null) this flag is
    // shared across every user, since there's a single row per announcement
    // rather than a per-user read receipt. Acceptable for this app's scale;
    // would need a join table to make broadcast read-state per-recipient.
    @Column(nullable = false)
    private boolean read;
}
