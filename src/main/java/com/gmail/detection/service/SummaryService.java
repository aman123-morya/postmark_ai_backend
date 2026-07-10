package com.gmail.detection.service;

import com.gmail.detection.dto.EmailSummaryDTO;

public interface SummaryService {

    EmailSummaryDTO generateSummary(Long emailId);

    EmailSummaryDTO getSummary(Long emailId);

}