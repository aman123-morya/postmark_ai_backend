package com.gmail.detection.service.imp;

import com.gmail.detection.entity.OtpToken;
import com.gmail.detection.enums.OtpPurpose;
import com.gmail.detection.exception.BadRequestException;
import com.gmail.detection.repository.OtpTokenRepository;
import com.gmail.detection.service.MailService;
import com.gmail.detection.service.OtpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpServiceImpl implements OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final OtpTokenRepository otpTokenRepository;
    private final MailService mailService;

    @Value("${app.otp.expiry-minutes:10}")
    private int expiryMinutes;

    public OtpServiceImpl(OtpTokenRepository otpTokenRepository, MailService mailService) {
        this.otpTokenRepository = otpTokenRepository;
        this.mailService = mailService;
    }

    @Override
    public String generateAndSend(String email, OtpPurpose purpose, String purposeLabel) {

        String code = String.format("%06d", RANDOM.nextInt(1_000_000));

        OtpToken token = OtpToken.builder()
                .email(email)
                .code(code)
                .purpose(purpose)
                .expiresAt(LocalDateTime.now().plusMinutes(expiryMinutes))
                .used(false)
                .build();

        otpTokenRepository.save(token);

        mailService.sendOtpEmail(email, code, purposeLabel, expiryMinutes);

        return code;
    }

    @Override
    public boolean isValid(String email, String code, OtpPurpose purpose) {

        return otpTokenRepository.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose)
                .filter(t -> t.getCode().equals(code))
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Override
    public void consume(String email, String code, OtpPurpose purpose) {

        OtpToken token = otpTokenRepository.findTopByEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose)
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP."));

        if (!token.getCode().equals(code)) {
            throw new BadRequestException("Invalid or expired OTP.");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("This OTP has expired. Please request a new one.");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);
    }
}
