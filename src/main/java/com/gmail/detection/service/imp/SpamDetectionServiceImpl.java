package com.gmail.detection.service.imp;

import com.gmail.detection.dto.SpamRecordDTO;
import com.gmail.detection.entity.Email;
import com.gmail.detection.entity.SpamRecord;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.repository.SpamRecordRepository;
import com.gmail.detection.service.SpamDetectionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpamDetectionServiceImpl implements SpamDetectionService {

    private final SpamRecordRepository spamRecordRepository;
    private final EmailRepository emailRepository;

    public SpamDetectionServiceImpl(
            SpamRecordRepository spamRecordRepository,
            EmailRepository emailRepository) {

        this.spamRecordRepository = spamRecordRepository;
        this.emailRepository = emailRepository;
    }

    @Override
    public SpamRecordDTO detectSpam(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        SpamRecord spamRecord = new SpamRecord();

        spamRecord.setSender(email.getSender());
        spamRecord.setSubject(email.getSubject());

        boolean spam = isSpam(email.getSubject(), email.getBody());

        spamRecord.setBlocked(spam);

        if (spam) {

            spamRecord.setReason("Spam keywords detected");

            email.setSpam(true);

        } else {

            spamRecord.setReason("Safe Email");

            email.setSpam(false);
        }

        emailRepository.save(email);

        SpamRecord savedRecord = spamRecordRepository.save(spamRecord);

        return convertToDTO(savedRecord);
    }

    @Override
    public List<SpamRecordDTO> getAllSpamRecords() {

        return spamRecordRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SpamRecordDTO getSpamRecordById(Long id) {

        SpamRecord spamRecord = spamRecordRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Spam Record not found with ID : " + id));

        return convertToDTO(spamRecord);
    }


    @Override
    public boolean isSpam(String subject, String body) {

        String text = ((subject == null ? "" : subject) + " "
                + (body == null ? "" : body)).toLowerCase();

        int score = 0;

        if (text.contains("lottery")) score += 3;
        if (text.contains("winner")) score += 3;
        if (text.contains("free money")) score += 3;
        if (text.contains("bitcoin")) score += 2;
        if (text.contains("claim prize")) score += 3;
        if (text.contains("click here")) score += 2;
        if (text.contains("congratulations")) score += 2;
        if (text.contains("limited time offer")) score += 2;

        // Only classify as spam if the score is high enough
        return score >= 4;
    }

    @Override
    public void deleteSpamRecord(Long id) {

        SpamRecord spamRecord = spamRecordRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Spam Record not found with ID : " + id));

        spamRecordRepository.delete(spamRecord);
    }

    // ====================================
    // Entity -> DTO
    // ====================================

    private SpamRecordDTO convertToDTO(SpamRecord spamRecord) {

        SpamRecordDTO dto = new SpamRecordDTO();

        dto.setId(spamRecord.getId());
        dto.setSender(spamRecord.getSender());
        dto.setSubject(spamRecord.getSubject());
        dto.setReason(spamRecord.getReason());
        dto.setBlocked(spamRecord.isBlocked());

        return dto;
    }
}