package com.gmail.detection.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment extends BaseEntity {

    @Column(nullable = false)
    private String emailSubject;

    @Column(nullable = false)
    private String assignedTo;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String status;

    private String remarks;

}