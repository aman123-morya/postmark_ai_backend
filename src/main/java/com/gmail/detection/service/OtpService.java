package com.gmail.detection.service;

import com.gmail.detection.enums.OtpPurpose;

public interface OtpService {

    /** Generates a new 6-digit OTP, persists it, emails it, and returns the code. */
    String generateAndSend(String email, OtpPurpose purpose, String purposeLabel);

    /** Validates (without consuming) that the given code is current and unexpired. */
    boolean isValid(String email, String code, OtpPurpose purpose);

    /** Validates and marks the OTP as used. Throws BadRequestException if invalid/expired. */
    void consume(String email, String code, OtpPurpose purpose);
}
