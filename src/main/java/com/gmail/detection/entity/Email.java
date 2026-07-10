package com.gmail.detection.entity;

import com.gmail.detection.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emails")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Email extends BaseEntity {


    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String receiver;

    @Column(nullable = false)
    private String subject;

    @Lob
    @Column(nullable = false)
    private String body;

    @Enumerated(EnumType.STRING)
    private Priority priority;


    @Enumerated(EnumType.STRING)
    private EmailStatus status;


    @Enumerated(EnumType.STRING)
    private DepartmentType department;


    @Enumerated(EnumType.STRING)
    private EmailCategory category;

    @Column(nullable = false)
    private boolean spam;

    @Column(nullable = false)
    private boolean starred;

    @Column(nullable = false)
    private boolean archived;

    @Enumerated(EnumType.STRING)
    private Sentiment sentiment;


    private LocalDateTime receivedTime;

    private LocalDateTime updatedTime;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String smartReply;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String reason;


    @Column(unique = true)
    private String gmailMessageId;

    // --- Extended AI analysis (Phase 3 extension) ---

    @Column(columnDefinition = "TEXT")
    private String keywords; // comma-separated

    @Column(columnDefinition = "TEXT")
    private String entities; // comma-separated

    private String language;

    private Boolean urgent;

    private Integer confidenceScore; // 0-100

    private Integer importanceScore; // 0-100

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