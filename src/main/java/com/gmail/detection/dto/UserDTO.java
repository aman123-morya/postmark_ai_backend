package com.gmail.detection.dto;

import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.Role;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private DepartmentType department;

    private boolean active;

    private boolean emailVerified;

    private String avatarUrl;

}