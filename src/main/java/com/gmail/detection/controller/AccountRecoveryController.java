package com.gmail.detection.controller;

import com.gmail.detection.dto.*;
import com.gmail.detection.entity.User;
import com.gmail.detection.enums.NotificationType;
import com.gmail.detection.enums.OtpPurpose;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.UserRepository;
import com.gmail.detection.service.AuditLogService;
import com.gmail.detection.service.NotificationService;
import com.gmail.detection.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Public account-recovery endpoints: forgot/reset password and email
 * verification via OTP. Mapped under /api/auth so they inherit the existing
 * "permitAll for /api/auth/**" rule in SecurityConfig - no security config
 * change needed for these to be reachable without a token.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AccountRecoveryController {

    private static final String MODULE = "ACCOUNT_RECOVERY";

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request,
                                                  HttpServletRequest httpRequest) {

        User user = findUserOrThrow(request.getEmail());

        otpService.generateAndSend(user.getEmail(), OtpPurpose.FORGOT_PASSWORD, "Password Reset");

        auditLogService.log(user.getEmail(), "FORGOT_PASSWORD", MODULE,
                "Password reset OTP requested", "SUCCESS", httpRequest);

        return ResponseEntity.ok("An OTP has been sent to " + user.getEmail() + ".");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {

        boolean valid = otpService.isValid(request.getEmail(), request.getOtp(), OtpPurpose.FORGOT_PASSWORD);
        return ResponseEntity.ok(valid);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                 HttpServletRequest httpRequest) {

        User user = findUserOrThrow(request.getEmail());

        otpService.consume(request.getEmail(), request.getOtp(), OtpPurpose.FORGOT_PASSWORD);

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.log(user.getEmail(), "RESET_PASSWORD", MODULE,
                "Password reset via OTP", "SUCCESS", httpRequest);

        notificationService.create(securityNotification(user.getEmail(),
                "Your password was reset. If this wasn't you, contact an admin immediately."));

        return ResponseEntity.ok("Password reset successfully. You can now log in.");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {

        User user = findUserOrThrow(request.getEmail());

        otpService.generateAndSend(user.getEmail(), OtpPurpose.EMAIL_VERIFICATION, "Email Verification");

        return ResponseEntity.ok("A new verification code has been sent to " + user.getEmail() + ".");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyOtpRequest request,
                                               HttpServletRequest httpRequest) {

        User user = findUserOrThrow(request.getEmail());

        otpService.consume(request.getEmail(), request.getOtp(), OtpPurpose.EMAIL_VERIFICATION);

        user.setEmailVerified(true);
        userRepository.save(user);

        auditLogService.log(user.getEmail(), "VERIFY_EMAIL", MODULE,
                "Email address verified", "SUCCESS", httpRequest);

        notificationService.create(securityNotification(user.getEmail(), "Your email address has been verified."));

        return ResponseEntity.ok("Email verified successfully.");
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for email : " + email));
    }

    private CreateNotificationRequest securityNotification(String email, String message) {
        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setRecipientEmail(email);
        req.setTitle("Security update");
        req.setMessage(message);
        req.setType(NotificationType.SECURITY);
        return req;
    }
}
