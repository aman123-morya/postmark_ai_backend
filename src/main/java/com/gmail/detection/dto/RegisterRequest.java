package com.gmail.detection.dto;

import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    private String email;

    @NotBlank
    private String password;

    private Role role;

    private DepartmentType department;

}