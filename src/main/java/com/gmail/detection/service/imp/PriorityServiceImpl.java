package com.gmail.detection.service.imp;

import com.gmail.detection.entity.Email;
import com.gmail.detection.enums.Priority;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.service.PriorityService;
import org.springframework.stereotype.Service;

@Service
public class PriorityServiceImpl implements PriorityService {

    private final EmailRepository emailRepository;

    public PriorityServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    // ==========================================
    // Detect Priority Using Email ID
    // ==========================================

    @Override
    public String detectPriority(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        return detectPriority(email.getSubject(), email.getBody());
    }

    // ==========================================
    // Detect Priority Using Subject & Body
    // ==========================================

    @Override
    public String detectPriority(String subject, String body) {

        String text = (subject + " " + body).toLowerCase();

        // ===========================
        // HIGH PRIORITY
        // ===========================

        if (text.contains("urgent")
                || text.contains("asap")
                || text.contains("critical")
                || text.contains("immediately")
                || text.contains("emergency")
                || text.contains("server down")
                || text.contains("payment failed")
                || text.contains("security")) {

            return Priority.HIGH.name();
        }

        // ===========================
        // MEDIUM PRIORITY
        // ===========================

        if (text.contains("meeting")
                || text.contains("review")
                || text.contains("approval")
                || text.contains("project")
                || text.contains("discussion")
                || text.contains("schedule")
                || text.contains("update")) {

            return Priority.MEDIUM.name();
        }

        // ===========================
        // LOW PRIORITY
        // ===========================

        return Priority.LOW.name();
    }

    // ==========================================
    // Update Email Priority
    // ==========================================

    @Override
    public void updatePriority(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        String priority = detectPriority(
                email.getSubject(),
                email.getBody());

        email.setPriority(Priority.valueOf(priority));

        emailRepository.save(email);
    }
}