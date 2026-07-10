package com.gmail.detection.entity;

import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {



    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Enumerated(EnumType.STRING)
    private DepartmentType department;

    @Column(nullable = false)
    private boolean active;

    // --- Account recovery / profile extension ---

    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerified = false;

    private String avatarUrl;
}