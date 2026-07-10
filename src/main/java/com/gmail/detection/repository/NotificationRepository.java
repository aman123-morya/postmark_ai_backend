package com.gmail.detection.repository;

import com.gmail.detection.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Personal notifications + broadcasts (recipientEmail is null), newest first.
    List<Notification> findByRecipientEmailOrRecipientEmailIsNullOrderByCreatedAtDesc(String recipientEmail);
}
