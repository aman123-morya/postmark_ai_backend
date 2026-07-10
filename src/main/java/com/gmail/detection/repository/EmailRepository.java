package com.gmail.detection.repository;

import com.gmail.detection.entity.Email;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.EmailCategory;
import com.gmail.detection.enums.EmailStatus;
import com.gmail.detection.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {

    Optional<Email> findByGmailMessageId(String gmailMessageId);

    List<Email> findBySender(String sender);

    List<Email> findByReceiver(String receiver);

    List<Email> findByPriority(Priority priority);

    List<Email> findByDepartment(DepartmentType department);

    List<Email> findByCategory(EmailCategory category);

    List<Email> findBySpam(boolean spam);

    List<Email> findByStarred(boolean starred);

    List<Email> findByStatus(EmailStatus status);
}