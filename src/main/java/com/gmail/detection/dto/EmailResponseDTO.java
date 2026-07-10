package com.gmail.detection.dto;

import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.EmailCategory;
import com.gmail.detection.enums.EmailStatus;
import com.gmail.detection.enums.Priority;
import com.gmail.detection.enums.Sentiment;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailResponseDTO {

    private Long id;

    private String gmailMessageId;

    private String sender;

    private String receiver;

    private String subject;

    private String body;

    private Priority priority;

    private DepartmentType department;

    private EmailCategory category;

    private EmailStatus status;

    private String spamStatus;

    private boolean starred;

    private boolean archived;

    private Sentiment sentiment;

    private String summary;

    private String message;

    private LocalDateTime receivedTime;

    private LocalDateTime updatedTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // --- Extended AI analysis (Phase 3 extension) ---

    private String keywords;

    private String entities;

    private String language;

    private Boolean urgent;

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