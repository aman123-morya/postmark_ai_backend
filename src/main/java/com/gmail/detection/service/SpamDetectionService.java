package com.gmail.detection.service;

import com.gmail.detection.dto.SpamRecordDTO;

import java.util.List;

public interface SpamDetectionService {

    SpamRecordDTO detectSpam(Long emailId);

    List<SpamRecordDTO> getAllSpamRecords();

    SpamRecordDTO getSpamRecordById(Long id);

    boolean isSpam(String subject, String body);

    void deleteSpamRecord(Long id);

}