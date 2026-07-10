package com.gmail.detection.service;

import com.gmail.detection.dto.EmailDTO;
import com.gmail.detection.dto.EmailResponseDTO;
import com.gmail.detection.dto.EmailUpdateRequest;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.EmailCategory;
import com.gmail.detection.enums.EmailStatus;
import com.gmail.detection.enums.Priority;

import java.util.List;

public interface EmailService {

    EmailResponseDTO sendEmail(EmailDTO emailDTO);

    EmailResponseDTO saveEmail(EmailDTO emailDTO);

    EmailResponseDTO getEmailById(Long id);

    List<EmailResponseDTO> getAllEmails();

    List<EmailResponseDTO> getEmailsBySender(String sender);

    List<EmailResponseDTO> getEmailsByReceiver(String receiver);

    List<EmailResponseDTO> getSpamEmails();

    List<EmailResponseDTO> getStarredEmails();

    List<EmailResponseDTO> getEmailsByPriority(Priority priority);

    List<EmailResponseDTO> getEmailsByCategory(EmailCategory category);

    List<EmailResponseDTO> getEmailsByDepartment(DepartmentType department);

    List<EmailResponseDTO> getEmailsByStatus(EmailStatus status);

    void deleteEmail(Long id);

    EmailResponseDTO updateEmail(Long id, EmailUpdateRequest request);

}