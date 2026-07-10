package com.gmail.detection.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "email_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSummary extends BaseEntity {

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String subject;

    @Column(length = 5000)
    private String originalText;

    @Column(length = 2000)
    private String summary;

}