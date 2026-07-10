package com.gmail.detection.service;

import com.gmail.detection.dto.AIResponseDTO;

public interface AIService {

    AIResponseDTO classifyEmail(Long emailId);

    AIResponseDTO summarizeEmail(Long emailId);

    AIResponseDTO generateSmartReply(Long emailId);

    AIResponseDTO generateSmartReply(Long emailId, String tone);

    AIResponseDTO analyzeCompleteEmail(Long emailId);

}