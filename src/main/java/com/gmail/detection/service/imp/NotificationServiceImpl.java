package com.gmail.detection.service.imp;

import com.gmail.detection.dto.CreateNotificationRequest;
import com.gmail.detection.dto.NotificationDTO;
import com.gmail.detection.entity.Notification;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.NotificationRepository;
import com.gmail.detection.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationDTO create(CreateNotificationRequest request) {

        Notification notification = Notification.builder()
                .recipientEmail(
                        (request.getRecipientEmail() == null || request.getRecipientEmail().isBlank())
                                ? null
                                : request.getRecipientEmail())
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .read(false)
                .build();

        return mapToDTO(notificationRepository.save(notification));
    }

    @Override
    public List<NotificationDTO> getForUser(String email) {

        return notificationRepository
                .findByRecipientEmailOrRecipientEmailIsNullOrderByCreatedAtDesc(email)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long unreadCount(String email) {

        return notificationRepository
                .findByRecipientEmailOrRecipientEmailIsNullOrderByCreatedAtDesc(email)
                .stream()
                .filter(n -> !n.isRead())
                .count();
    }

    @Override
    public NotificationDTO markRead(Long id, String email) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID : " + id));

        notification.setRead(true);

        return mapToDTO(notificationRepository.save(notification));
    }

    @Override
    public void markAllRead(String email) {

        List<Notification> notifications = notificationRepository
                .findByRecipientEmailOrRecipientEmailIsNullOrderByCreatedAtDesc(email);

        notifications.forEach(n -> n.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void delete(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID : " + id));

        notificationRepository.delete(notification);
    }

    private NotificationDTO mapToDTO(Notification notification) {

        NotificationDTO dto = new NotificationDTO();

        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }
}
