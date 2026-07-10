package com.gmail.detection.service.impl;

import com.gmail.detection.dto.EmailSummaryDTO;
import com.gmail.detection.entity.Email;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.service.SummaryService;
import org.springframework.stereotype.Service;

@Service
public class SummaryServiceImpl implements SummaryService {

    private final EmailRepository emailRepository;

    public SummaryServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    // =====================================
    // Generate Summary
    // =====================================

    @Override
    public EmailSummaryDTO generateSummary(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        String body = email.getBody();

        String summary;

        if (body == null || body.isBlank()) {
            summary = "No email content available.";
        }
        else if (body.length() <= 150) {
            summary = body;
        }
        else {
            summary = body.substring(0, 150) + "...";
        }

        // Save summary into Email table
        email.setSummary(summary);
        emailRepository.save(email);

        return convertToDTO(email);
    }

    // =====================================
    // Get Summary
    // =====================================

    @Override
    public EmailSummaryDTO getSummary(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        return convertToDTO(email);
    }

    // =====================================
    // Convert Entity -> DTO
    // =====================================

    private EmailSummaryDTO convertToDTO(Email email) {

        EmailSummaryDTO dto = new EmailSummaryDTO();

        dto.setId(email.getId());
        dto.setSender(email.getSender());
        dto.setSubject(email.getSubject());
        dto.setOriginalText(email.getBody());
        dto.setSummary(email.getSummary());

        return dto;
    }
}