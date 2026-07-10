package com.gmail.detection.dto;

import lombok.Data;

import java.util.List;

@Data
public class AIResponseDTO {

    // Raw AI response (optional, useful for debugging/logging)
    private String response;

    // Core Email Classification
    private String category;
    private String priority;
    private String department;
    private String sentiment;
    private Boolean spam;

    // Core AI Features
    private String summary;
    private String smartReply;

    // Optional explanation from AI
    private String reason;

    // --- Extended AI analysis (Phase 3 extension) ---

    private List<String> keywords;
    private List<String> entities;

    private String language;
    private Boolean urgent;

    // 0-100 scale
    private Integer confidenceScore;
    private Integer importanceScore;

    private Boolean meetingDetected;
    private Boolean deadlineDetected;
    private Boolean taskDetected;
    private Boolean actionRequired;
    private Boolean riskDetected;

    private String emotion;
    private String intent;
    private String topic;

    private String suggestedLabel;
    private String suggestedFolder;

    private Boolean autoArchiveSuggested;
    private Boolean autoDeleteSuggested;
    private Boolean followUpSuggested;
    private Boolean reminderSuggested;
}
