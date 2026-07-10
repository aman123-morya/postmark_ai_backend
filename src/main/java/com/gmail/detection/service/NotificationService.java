package com.gmail.detection.service;

import com.gmail.detection.dto.CreateNotificationRequest;
import com.gmail.detection.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {

    NotificationDTO create(CreateNotificationRequest request);

    List<NotificationDTO> getForUser(String email);

    long unreadCount(String email);

    NotificationDTO markRead(Long id, String email);

    void markAllRead(String email);

    void delete(Long id);
}
