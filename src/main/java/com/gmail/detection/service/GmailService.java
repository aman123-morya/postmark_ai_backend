package com.gmail.detection.service;

import com.gmail.detection.dto.EmailDTO;
import com.gmail.detection.dto.EmailResponseDTO;

import java.util.List;

public interface GmailService {

    EmailResponseDTO sendEmail(EmailDTO emailDTO);

    List<EmailResponseDTO> readInbox();

    List<EmailResponseDTO> readSentEmails();

    List<EmailResponseDTO> readDrafts();

    EmailResponseDTO getEmailById(String gmailMessageId);

    void deleteEmail(String gmailMessageId);

    void createLabel(String labelName);

    void applyLabel(String gmailMessageId, String labelName);

    void forwardEmail(String gmailMessageId, String receiverEmail);

    void syncInbox();

    void syncSentEmails();

    void syncDrafts();
}