package com.gmail.detection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned to the client after a successful login or registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;

    private String refreshToken;

    @Builder.Default
    private String type = "Bearer";

    private Long userId;

    private String email;

    private String firstName;

    private String lastName;

    private String role;
}
