package com.gmail.detection.dto;

import com.gmail.detection.enums.DepartmentType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private DepartmentType department;
}
