package com.gmail.detection.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "spam_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpamRecord extends BaseEntity {

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String subject;

    @Column(length = 1000)
    private String reason;

    private boolean blocked;

}