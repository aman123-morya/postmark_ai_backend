package com.gmail.detection.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendVerificationRequest {

    @Email(message = "Invalid Email")
    @NotBlank(message = "Email is required")
    private String email;
}
