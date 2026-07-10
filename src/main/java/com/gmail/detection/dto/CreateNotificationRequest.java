package com.gmail.detection.dto;

import com.gmail.detection.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateNotificationRequest {

    // null/blank = broadcast to all users
    private String recipientEmail;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    private NotificationType type = NotificationType.INFO;
}
