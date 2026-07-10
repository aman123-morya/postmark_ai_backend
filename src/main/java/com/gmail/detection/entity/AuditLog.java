package com.gmail.detection.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String module;

    @Column(length = 1000)
    private String description;

    private String ipAddress;

    private String status;

}