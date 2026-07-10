package com.gmail.detection.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "labels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Label extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String labelName;

    @Column(length = 500)
    private String description;

}