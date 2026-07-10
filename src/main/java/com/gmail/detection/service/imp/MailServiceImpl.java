package com.gmail.detection.service.imp;

import com.gmail.detection.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends OTP emails via Spring Mail (SMTP configured through spring.mail.* in
 * application.properties). If SMTP isn't configured/reachable in a local dev
 * environment, the failure is logged (with the OTP code) instead of blowing
 * up the request - so forgot-password/verify-email flows still work end to
 * end during local development without real mail credentials.
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@gmail-management-system.local}")
    private String fromAddress;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otpCode, String purposeLabel, int expiryMinutes) {

        String subject = purposeLabel + " - Your verification code";
        String body = "Your " + purposeLabel.toLowerCase() + " code is: " + otpCode
                + "\n\nThis code expires in " + expiryMinutes + " minutes. "
                + "If you did not request this, you can safely ignore this email.";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Could not send email to {} (SMTP not configured?). OTP for {} is: {}",
                    toEmail, purposeLabel, otpCode, ex);
        }
    }
}
