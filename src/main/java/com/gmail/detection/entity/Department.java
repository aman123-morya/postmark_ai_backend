package com.gmail.detection.entity;

import jakarta.persistence.*;
        import lombok.*;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {


    @Column(nullable = false, unique = true)
    private String departmentName;

    @Column(length = 500)
    private String description;
}