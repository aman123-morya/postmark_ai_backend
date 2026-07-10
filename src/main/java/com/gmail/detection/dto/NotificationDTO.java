package com.gmail.detection.dto;

import com.gmail.detection.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {

    private Long id;

    private String title;

    private String message;

    private NotificationType type;

    private boolean read;

    private LocalDateTime createdAt;
}
