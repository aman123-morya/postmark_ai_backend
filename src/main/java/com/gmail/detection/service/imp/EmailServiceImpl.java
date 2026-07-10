package com.gmail.detection.service.imp;

import com.gmail.detection.dto.EmailDTO;
import com.gmail.detection.dto.EmailResponseDTO;
import com.gmail.detection.dto.EmailUpdateRequest;
import com.gmail.detection.entity.Email;
import com.gmail.detection.enums.EmailStatus;
import com.gmail.detection.enums.Sentiment;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.service.EmailService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;

    public EmailServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public EmailResponseDTO sendEmail(EmailDTO emailDTO) {

        Email email = convertToEntity(emailDTO);

        email.setStatus(EmailStatus.SENT);
        email.setSpam(false);
        email.setStarred(false);
        email.setArchived(false);
        email.setSentiment(Sentiment.NEUTRAL);
        email.setSummary("");
        email.setReceivedTime(LocalDateTime.now());
        email.setUpdatedTime(LocalDateTime.now());

        Email savedEmail = emailRepository.save(email);

        return convertToDTO(savedEmail);
    }

    @Override
    public EmailResponseDTO saveEmail(EmailDTO emailDTO) {

        Email email = convertToEntity(emailDTO);

        email.setStatus(EmailStatus.DRAFT);
        email.setSpam(false);
        email.setStarred(false);
        email.setArchived(false);
        email.setSentiment(Sentiment.NEUTRAL);
        email.setSummary("");
        email.setReceivedTime(LocalDateTime.now());
        email.setUpdatedTime(LocalDateTime.now());

        Email savedEmail = emailRepository.save(email);

        return convertToDTO(savedEmail);
    }

    @Override
    public EmailResponseDTO getEmailById(Long id) {

        Email email = emailRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Email not found with ID : " + id));

        return convertToDTO(email);
    }

    @Override
    public List<EmailResponseDTO> getAllEmails() {

        return emailRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getEmailsBySender(String sender) {

        return emailRepository.findBySender(sender)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getEmailsByReceiver(String receiver) {

        return emailRepository.findByReceiver(receiver)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getSpamEmails() {

        return emailRepository.findBySpam(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getStarredEmails() {

        return emailRepository.findByStarred(true)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getEmailsByPriority(com.gmail.detection.enums.Priority priority) {

        return emailRepository.findByPriority(priority)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getEmailsByCategory(com.gmail.detection.enums.EmailCategory category) {

        return emailRepository.findByCategory(category)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getEmailsByDepartment(com.gmail.detection.enums.DepartmentType department) {

        return emailRepository.findByDepartment(department)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmailResponseDTO> getEmailsByStatus(com.gmail.detection.enums.EmailStatus status) {

        return emailRepository.findByStatus(status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEmail(Long id) {

        Email email = emailRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Email not found with ID : " + id));

        emailRepository.delete(email);
    }

    @Override
    public EmailResponseDTO updateEmail(Long id, EmailUpdateRequest request) {

        Email email = emailRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Email not found with ID : " + id));

        if (request.getStarred() != null) {
            email.setStarred(request.getStarred());
        }
        if (request.getArchived() != null) {
            email.setArchived(request.getArchived());
        }
        if (request.getStatus() != null) {
            email.setStatus(request.getStatus());
        }
        email.setUpdatedTime(LocalDateTime.now());

        return convertToDTO(emailRepository.save(email));
    }

    // ==========================
    // DTO -> Entity
    // ==========================

    private Email convertToEntity(EmailDTO dto) {

        Email email = new Email();

        email.setSender(dto.getSender());
        email.setReceiver(dto.getReceiver());
        email.setSubject(dto.getSubject());
        email.setBody(dto.getBody());
        email.setPriority(dto.getPriority());
        email.setDepartment(dto.getDepartment());
        email.setCategory(dto.getCategory());

        return email;
    }

    // ==========================
    // Entity -> DTO
    // ==========================

    private EmailResponseDTO convertToDTO(Email email) {

        EmailResponseDTO dto = new EmailResponseDTO();

        dto.setId(email.getId());
        dto.setSender(email.getSender());
        dto.setReceiver(email.getReceiver());
        dto.setSubject(email.getSubject());
        dto.setBody(email.getBody());
        dto.setPriority(email.getPriority());
        dto.setDepartment(email.getDepartment());
        dto.setCategory(email.getCategory());
        dto.setStatus(email.getStatus());
        dto.setSpamStatus(email.isSpam() ? "Positive" : "Negative");
        dto.setStarred(email.isStarred());
        dto.setArchived(email.isArchived());
        dto.setSentiment(email.getSentiment());
        dto.setSummary(email.getSummary());
        dto.setReceivedTime(email.getReceivedTime());
        dto.setUpdatedTime(email.getUpdatedTime());
        dto.setCreatedAt(email.getCreatedAt());
        dto.setUpdatedAt(email.getUpdatedAt());

        dto.setKeywords(email.getKeywords());
        dto.setEntities(email.getEntities());
        dto.setLanguage(email.getLanguage());
        dto.setUrgent(email.getUrgent());
        dto.setConfidenceScore(email.getConfidenceScore());
        dto.setImportanceScore(email.getImportanceScore());
        dto.setMeetingDetected(email.getMeetingDetected());
        dto.setDeadlineDetected(email.getDeadlineDetected());
        dto.setTaskDetected(email.getTaskDetected());
        dto.setActionRequired(email.getActionRequired());
        dto.setRiskDetected(email.getRiskDetected());
        dto.setEmotion(email.getEmotion());
        dto.setIntent(email.getIntent());
        dto.setTopic(email.getTopic());
        dto.setSuggestedLabel(email.getSuggestedLabel());
        dto.setSuggestedFolder(email.getSuggestedFolder());
        dto.setAutoArchiveSuggested(email.getAutoArchiveSuggested());
        dto.setAutoDeleteSuggested(email.getAutoDeleteSuggested());
        dto.setFollowUpSuggested(email.getFollowUpSuggested());
        dto.setReminderSuggested(email.getReminderSuggested());

        return dto;
    }
}