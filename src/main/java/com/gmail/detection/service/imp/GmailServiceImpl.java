package com.gmail.detection.service.imp;

import com.gmail.detection.dto.AIResponseDTO;
import com.gmail.detection.entity.Email;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.Priority;
import com.gmail.detection.enums.Sentiment;
import com.gmail.detection.service.*;
import com.google.api.services.gmail.model.*;
import com.gmail.detection.enums.EmailStatus;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Properties;

import com.gmail.detection.dto.EmailDTO;
import com.gmail.detection.dto.EmailResponseDTO;
import com.gmail.detection.repository.EmailRepository;
import com.google.api.services.gmail.Gmail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.gmail.detection.enums.EmailCategory;

@Service
@RequiredArgsConstructor
public class GmailServiceImpl implements GmailService {

    private final Gmail gmail;
    private final EmailRepository emailRepository;
    private final AIService aiService;
    private EmailCategory mapCategory(String category) {

        if (category == null) {
            return EmailCategory.OTHER;
        }

        switch (category.trim().toUpperCase()) {

            case "WORK":
                return EmailCategory.WORK;

            case "PERSONAL":
                return EmailCategory.PERSONAL;

            case "SOCIAL":
                return EmailCategory.SOCIAL;

            case "SUPPORT":
                return EmailCategory.SUPPORT;

            case "FINANCE":
                return EmailCategory.FINANCE;

            case "PROMOTION":
            case "MARKETING":
            case "ADVERTISEMENT":
            case "NEWSLETTER":
                return EmailCategory.PROMOTION;

            default:
                return EmailCategory.OTHER;
        }
    }
    private DepartmentType mapDepartment(String department) {

        if (department == null) {
            return DepartmentType.GENERAL;
        }

        switch (department.trim().toUpperCase()) {

            case "HR":
                return DepartmentType.HR;

            case "IT":
                return DepartmentType.IT;

            case "FINANCE":
                return DepartmentType.FINANCE;

            case "SUPPORT":
                return DepartmentType.SUPPORT;

            case "SALES":
                return DepartmentType.SALES;

            case "MARKETING":
                return DepartmentType.MARKETING;

            default:
                return DepartmentType.GENERAL;
        }
    }
    private Priority mapPriority(String priority) {

        if (priority == null) {
            return Priority.LOW;
        }

        switch (priority.trim().toUpperCase()) {

            case "HIGH":
                return Priority.HIGH;

            case "MEDIUM":
                return Priority.MEDIUM;

            default:
                return Priority.LOW;
        }
    }
    private Sentiment mapSentiment(String sentiment) {

        if (sentiment == null) {
            return Sentiment.NEUTRAL;
        }

        switch (sentiment.trim().toUpperCase()) {

            case "POSITIVE":
                return Sentiment.POSITIVE;

            case "NEGATIVE":
                return Sentiment.NEGATIVE;

            default:
                return Sentiment.NEUTRAL;
        }
    }


    @Override
    public EmailResponseDTO sendEmail(EmailDTO emailDTO) {

        try {

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage mimeMessage = new MimeMessage(session);

            // ❌ DO NOT use user input sender
            mimeMessage.setFrom(new InternetAddress("me"));

            mimeMessage.addRecipient(
                    jakarta.mail.Message.RecipientType.TO,
                    new InternetAddress(emailDTO.getReceiver())
            );

            mimeMessage.setSubject(emailDTO.getSubject());
            mimeMessage.setText(emailDTO.getBody());

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mimeMessage.writeTo(buffer);

            byte[] bytes = buffer.toByteArray();

            String encodedEmail =
                    Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(bytes);

            Message message = new Message();
            message.setRaw(encodedEmail);

            Message sentMessage = gmail.users()
                    .messages()
                    .send("me", message)
                    .execute();

            Email email = new Email();
            email.setSender("me@gmail.com"); // IMPORTANT FIX
            email.setReceiver(emailDTO.getReceiver());
            email.setSubject(emailDTO.getSubject());
            email.setBody(emailDTO.getBody());
            email.setStatus(EmailStatus.SENT);
            email.setPriority(emailDTO.getPriority());      // ✅ ADD THIS
            email.setCategory(emailDTO.getCategory());      // ✅ ADD THIS
            email.setDepartment(emailDTO.getDepartment());  // ✅ ADD THIS
            email.setSpam(false);
            email.setStarred(false);
            email.setArchived(false);

            email.setReceivedTime(LocalDateTime.now());
            email.setUpdatedTime(LocalDateTime.now());




            emailRepository.save(email);

            EmailResponseDTO response = new EmailResponseDTO();
            response.setGmailMessageId(sentMessage.getId());
            response.setSender("me@gmail.com");
            response.setReceiver(emailDTO.getReceiver());
            response.setSubject(emailDTO.getSubject());
            response.setBody(emailDTO.getBody());
            response.setMessage("Email sent successfully.");

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public List<EmailResponseDTO> readInbox() {

        try {

            List<EmailResponseDTO> emails = new ArrayList<>();

            ListMessagesResponse response =
                    gmail.users()
                            .messages()
                            .list("me")
                            .setMaxResults(20L)
                            .execute();

            if (response.getMessages() == null) {
                return emails;
            }

            for (Message message : response.getMessages()) {

                Message fullMessage = gmail.users()
                        .messages()
                        .get("me", message.getId())
                        .setFormat("full")
                        .execute();

                EmailResponseDTO dto = new EmailResponseDTO();

                dto.setGmailMessageId(fullMessage.getId());

                String subject = "";
                String from = "";
                String to = "";

                if (fullMessage.getPayload() != null &&
                        fullMessage.getPayload().getHeaders() != null) {

                    for (MessagePartHeader header :
                            fullMessage.getPayload().getHeaders()) {
                        if ("Subject".equalsIgnoreCase(header.getName())) {
                            subject = header.getValue();
                        }

                        if ("From".equalsIgnoreCase(header.getName())) {
                            from = header.getValue();
                        }


                        if ("To".equalsIgnoreCase(header.getName())) {
                            to = header.getValue();
                        }
                    }
                }

                dto.setSubject(subject);
                dto.setSender(from);
                dto.setReceiver(to);

                if (fullMessage.getSnippet() != null) {
                    dto.setBody(fullMessage.getSnippet());
                }

                emails.add(dto);
            }

            return emails;

        } catch (Exception e) {

            throw new RuntimeException("Unable to read inbox : "
                    + e.getMessage());

        }

    }

    @Override
    public List<EmailResponseDTO> readSentEmails() {

        try {

            List<EmailResponseDTO> emails = new ArrayList<>();

            ListMessagesResponse response =
                    gmail.users()
                            .messages()
                            .list("me")
                            .setLabelIds(Collections.singletonList("SENT"))
                            .setMaxResults(20L)
                            .execute();

            if (response.getMessages() == null) {
                return emails;
            }

            for (Message message : response.getMessages()) {

                Message fullMessage = gmail.users()
                        .messages()
                        .get("me", message.getId())
                        .setFormat("full")
                        .execute();

                EmailResponseDTO dto = new EmailResponseDTO();

                dto.setGmailMessageId(fullMessage.getId());

                String subject = "";
                String from = "";
                String to = "";

                if (fullMessage.getPayload() != null &&
                        fullMessage.getPayload().getHeaders() != null) {

                    for (MessagePartHeader header :
                            fullMessage.getPayload().getHeaders()) {

                        if ("Subject".equalsIgnoreCase(header.getName())) {
                            subject = header.getValue();
                        }

                        if ("From".equalsIgnoreCase(header.getName())) {
                            from = header.getValue();
                        }

                        if ("To".equalsIgnoreCase(header.getName())) {
                            to = header.getValue();
                        }
                    }
                }

                dto.setSubject(subject);
                dto.setSender(from);
                dto.setReceiver(to);

                if (fullMessage.getSnippet() != null) {
                    dto.setBody(fullMessage.getSnippet());
                }

                emails.add(dto);
            }

            return emails;

        } catch (Exception e) {

            throw new RuntimeException("Unable to read sent emails : "
                    + e.getMessage());

        }
    }

    @Override
    public List<EmailResponseDTO> readDrafts() {

        try {

            List<EmailResponseDTO> emails = new ArrayList<>();

            com.google.api.services.gmail.model.ListDraftsResponse response =
                    gmail.users()
                            .drafts()
                            .list("me")
                            .setMaxResults(20L)
                            .execute();

            if (response.getDrafts() == null) {
                return emails;
            }

            for (com.google.api.services.gmail.model.Draft draft : response.getDrafts()) {

                com.google.api.services.gmail.model.Draft fullDraft =
                        gmail.users()
                                .drafts()
                                .get("me", draft.getId())
                                .setFormat("full")
                                .execute();

                Message message = fullDraft.getMessage();

                EmailResponseDTO dto = new EmailResponseDTO();

                // Keyed on the draft ID (stable per draft) rather than the
                // underlying message ID, which Gmail rewrites every time a
                // draft is edited.
                dto.setGmailMessageId("draft-" + draft.getId());

                String subject = "";
                String from = "";
                String to = "";

                if (message != null && message.getPayload() != null &&
                        message.getPayload().getHeaders() != null) {

                    for (MessagePartHeader header : message.getPayload().getHeaders()) {

                        if ("Subject".equalsIgnoreCase(header.getName())) {
                            subject = header.getValue();
                        }
                        if ("From".equalsIgnoreCase(header.getName())) {
                            from = header.getValue();
                        }
                        if ("To".equalsIgnoreCase(header.getName())) {
                            to = header.getValue();
                        }
                    }
                }

                dto.setSubject(subject);
                dto.setSender(from);
                dto.setReceiver(to);

                if (message != null && message.getSnippet() != null) {
                    dto.setBody(message.getSnippet());
                }

                emails.add(dto);
            }

            return emails;

        } catch (Exception e) {
            throw new RuntimeException("Unable to read drafts : " + e.getMessage());
        }
    }

    @Override
    public EmailResponseDTO getEmailById(String gmailMessageId) {

        // Gmail API code will be added later
        return new EmailResponseDTO();
    }

    @Override
    public void deleteEmail(String gmailMessageId) {

        try {

            gmail.users()
                    .messages()
                    .trash("me", gmailMessageId)
                    .execute();

            System.out.println("Email moved to Trash.");

        } catch (Exception e) {

            throw new RuntimeException("Unable to delete email : " + e.getMessage());

        }
    }

    @Override
    public void createLabel(String labelName) {

        try {

            com.google.api.services.gmail.model.Label label =
                    new com.google.api.services.gmail.model.Label();

            label.setName(labelName);

            // Required fields
            label.setLabelListVisibility("labelShow");
            label.setMessageListVisibility("show");

            gmail.users()
                    .labels()
                    .create("me", label)
                    .execute();

            System.out.println("Label created successfully.");

        } catch (Exception e) {

            throw new RuntimeException("Unable to create label : " + e.getMessage());

        }
    }

    @Override
    public void applyLabel(String gmailMessageId, String labelName) {

        try {

            // Get all Gmail labels
            ListLabelsResponse labelsResponse = gmail.users()
                    .labels()
                    .list("me")
                    .execute();

            String labelId = null;

            // Find label ID by label name
            for (Label label : labelsResponse.getLabels()) {

                if (label.getName().equalsIgnoreCase(labelName)) {

                    labelId = label.getId();

                    break;
                }
            }

            if (labelId == null) {

                throw new RuntimeException("Label not found : " + labelName);

            }

            ModifyMessageRequest request = new ModifyMessageRequest();

            request.setAddLabelIds(Collections.singletonList(labelId));

            gmail.users()
                    .messages()
                    .modify("me", gmailMessageId, request)
                    .execute();

            System.out.println("Label applied successfully.");

        } catch (Exception e) {

            throw new RuntimeException("Unable to apply label : "
                    + e.getMessage());

        }
    }

    @Override
    public void forwardEmail(String gmailMessageId, String receiverEmail) {

        // Gmail API code will be added later

    }

    @Override
    public void syncInbox() {

        try {

            System.out.println("========== Inbox Sync Started ==========");

            List<EmailResponseDTO> inboxEmails = readInbox();

            System.out.println("Total Emails Found : " + inboxEmails.size());

            for (EmailResponseDTO dto : inboxEmails) {

                // Skip duplicate emails
                if (emailRepository.findByGmailMessageId(dto.getGmailMessageId()).isPresent()) {

                    System.out.println("Duplicate Email : " + dto.getSubject());

                    continue;
                }

                Email email = new Email();

                email.setGmailMessageId(dto.getGmailMessageId());
                email.setSender(dto.getSender());
                email.setReceiver(dto.getReceiver());
                email.setSubject(dto.getSubject());
                email.setBody(dto.getBody());

                // Optional fields
                email.setPriority(dto.getPriority());
                email.setDepartment(dto.getDepartment());
                email.setCategory(dto.getCategory());
                email.setStatus(EmailStatus.RECEIVED);

                email.setReceivedTime(LocalDateTime.now());

                email.setUpdatedTime(LocalDateTime.now());

                email.setArchived(false);

                email.setStarred(false);

                email.setSpam(false);


                Email savedEmail = emailRepository.save(email);
                System.out.println("Saved Email ID = " + savedEmail.getId());

                System.out.println(
                        emailRepository.findById(savedEmail.getId()).isPresent()
                );

                AIResponseDTO ai = aiService.analyzeCompleteEmail(savedEmail.getId());
                System.out.println("Category : " + ai.getCategory());
                System.out.println("Department : " + ai.getDepartment());
                System.out.println("Priority : " + ai.getPriority());
                System.out.println("Sentiment : " + ai.getSentiment());
                System.out.println("Summary : " + ai.getSummary());
                System.out.println("Smart Reply : " + ai.getSmartReply());
                System.out.println("Reason : " + ai.getReason());
                savedEmail.setCategory(
                        mapCategory(ai.getCategory())
                );

// Department
                savedEmail.setDepartment(
                        mapDepartment(ai.getDepartment())
                );

// Priority
                savedEmail.setPriority(
                        mapPriority(ai.getPriority())
                );

// Sentiment
                savedEmail.setSentiment(
                        mapSentiment(ai.getSentiment())
                );

// Spam
                savedEmail.setSpam(
                        ai.getSpam() != null
                                ? ai.getSpam()
                                : false
                );

// Summary
                savedEmail.setSummary(
                        ai.getSummary() != null
                                ? ai.getSummary()
                                : ""
                );

// Smart Reply
                savedEmail.setSmartReply(
                        ai.getSmartReply() != null
                                ? ai.getSmartReply()
                                : ""
                );

// Reason
                savedEmail.setReason(
                        ai.getReason() != null
                                ? ai.getReason()
                                : ""
                );

// Extended AI fields
                savedEmail.setKeywords(
                        ai.getKeywords() != null
                                ? String.join(", ", ai.getKeywords())
                                : ""
                );

                savedEmail.setEntities(
                        ai.getEntities() != null
                                ? String.join(", ", ai.getEntities())
                                : ""
                );

                savedEmail.setLanguage(ai.getLanguage());
                savedEmail.setUrgent(Boolean.TRUE.equals(ai.getUrgent()));
                savedEmail.setConfidenceScore(ai.getConfidenceScore());
                savedEmail.setImportanceScore(ai.getImportanceScore());
                savedEmail.setMeetingDetected(Boolean.TRUE.equals(ai.getMeetingDetected()));
                savedEmail.setDeadlineDetected(Boolean.TRUE.equals(ai.getDeadlineDetected()));
                savedEmail.setTaskDetected(Boolean.TRUE.equals(ai.getTaskDetected()));
                savedEmail.setActionRequired(Boolean.TRUE.equals(ai.getActionRequired()));
                savedEmail.setRiskDetected(Boolean.TRUE.equals(ai.getRiskDetected()));
                savedEmail.setEmotion(ai.getEmotion());
                savedEmail.setIntent(ai.getIntent());
                savedEmail.setTopic(ai.getTopic());
                savedEmail.setSuggestedLabel(ai.getSuggestedLabel());
                savedEmail.setSuggestedFolder(ai.getSuggestedFolder());
                savedEmail.setAutoArchiveSuggested(Boolean.TRUE.equals(ai.getAutoArchiveSuggested()));
                savedEmail.setAutoDeleteSuggested(Boolean.TRUE.equals(ai.getAutoDeleteSuggested()));
                savedEmail.setFollowUpSuggested(Boolean.TRUE.equals(ai.getFollowUpSuggested()));
                savedEmail.setReminderSuggested(Boolean.TRUE.equals(ai.getReminderSuggested()));

                savedEmail.setUpdatedTime(LocalDateTime.now());

                if (savedEmail.getReceivedTime() == null) {
                    savedEmail.setReceivedTime(LocalDateTime.now());
                }

                if (savedEmail.getStatus() == null) {
                    savedEmail.setStatus(EmailStatus.RECEIVED);
                }
                emailRepository.save(savedEmail);

                System.out.println("AI Analysis Completed : " + savedEmail.getSubject());
            }

            System.out.println("========== Inbox Sync Completed ==========");

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("Unable to sync inbox : " + e.getMessage());
        }
    }

    @Override
    public void syncSentEmails() {

        try {

            System.out.println("========== Sent Sync Started ==========");

            List<EmailResponseDTO> sentEmails = readSentEmails();

            System.out.println("Total Sent Emails Found : " + sentEmails.size());

            for (EmailResponseDTO dto : sentEmails) {

                if (emailRepository.findByGmailMessageId(dto.getGmailMessageId()).isPresent()) {
                    System.out.println("Duplicate Sent Email : " + dto.getSubject());
                    continue;
                }

                Email email = new Email();

                email.setGmailMessageId(dto.getGmailMessageId());
                email.setSender(dto.getSender());
                email.setReceiver(dto.getReceiver());
                email.setSubject(dto.getSubject());
                email.setBody(dto.getBody());
                email.setPriority(dto.getPriority());
                email.setDepartment(dto.getDepartment());
                email.setCategory(dto.getCategory());
                email.setStatus(EmailStatus.SENT);
                email.setReceivedTime(LocalDateTime.now());
                email.setUpdatedTime(LocalDateTime.now());
                email.setArchived(false);
                email.setStarred(false);
                email.setSpam(false);

                emailRepository.save(email);
            }

            System.out.println("========== Sent Sync Completed ==========");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to sync sent emails : " + e.getMessage());
        }
    }

    @Override
    public void syncDrafts() {

        try {

            System.out.println("========== Drafts Sync Started ==========");

            List<EmailResponseDTO> drafts = readDrafts();

            System.out.println("Total Drafts Found : " + drafts.size());

            for (EmailResponseDTO dto : drafts) {

                if (emailRepository.findByGmailMessageId(dto.getGmailMessageId()).isPresent()) {
                    System.out.println("Duplicate Draft : " + dto.getSubject());
                    continue;
                }

                Email email = new Email();

                email.setGmailMessageId(dto.getGmailMessageId());
                email.setSender(dto.getSender());
                email.setReceiver(dto.getReceiver());
                email.setSubject(dto.getSubject());
                email.setBody(dto.getBody());
                email.setPriority(dto.getPriority());
                email.setDepartment(dto.getDepartment());
                email.setCategory(dto.getCategory());
                email.setStatus(EmailStatus.DRAFT);
                email.setReceivedTime(LocalDateTime.now());
                email.setUpdatedTime(LocalDateTime.now());
                email.setArchived(false);
                email.setStarred(false);
                email.setSpam(false);

                emailRepository.save(email);
            }

            System.out.println("========== Drafts Sync Completed ==========");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to sync drafts : " + e.getMessage());
        }
    }
}