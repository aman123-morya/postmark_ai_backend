package com.gmail.detection.service;

public interface MailService {

    void sendOtpEmail(String toEmail, String otpCode, String purposeLabel, int expiryMinutes);
}
