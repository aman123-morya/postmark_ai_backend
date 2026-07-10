package com.gmail.detection.controller;

import com.gmail.detection.dto.ChangePasswordRequest;
import com.gmail.detection.dto.ProfileUpdateRequest;
import com.gmail.detection.dto.UserDTO;
import com.gmail.detection.entity.AuditLog;
import com.gmail.detection.repository.AuditLogRepository;
import com.gmail.detection.service.AuditLogService;
import com.gmail.detection.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Self-service profile endpoints for the currently authenticated user
 * (any role). Distinct from /api/users/**, which is the admin-only
 * management API for editing *other* users.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private static final String MODULE = "PROFILE";

    private final ProfileService profileService;
    private final AuditLogService auditLogService;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getMyProfile(authentication.getName()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMyProfile(@Valid @RequestBody ProfileUpdateRequest request,
                                                    Authentication authentication, HttpServletRequest httpRequest) {

        UserDTO updated = profileService.updateMyProfile(authentication.getName(), request);

        auditLogService.log(authentication.getName(), "UPDATE_PROFILE", MODULE,
                "Updated own profile", "SUCCESS", httpRequest);

        return ResponseEntity.ok(updated);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                  Authentication authentication, HttpServletRequest httpRequest) {

        profileService.changePassword(authentication.getName(), request);

        auditLogService.log(authentication.getName(), "CHANGE_PASSWORD", MODULE,
                "Changed own password", "SUCCESS", httpRequest);

        return ResponseEntity.ok("Password updated successfully.");
    }

    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<UserDTO> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                 Authentication authentication, HttpServletRequest httpRequest) {

        UserDTO updated = profileService.uploadAvatar(authentication.getName(), file);

        auditLogService.log(authentication.getName(), "UPDATE_AVATAR", MODULE,
                "Updated profile avatar", "SUCCESS", httpRequest);

        return ResponseEntity.ok(updated);
    }

    // Login-device/activity history, reusing the existing audit trail rather
    // than standing up a separate device-tracking table.
    @GetMapping("/login-activity")
    public ResponseEntity<List<AuditLog>> loginActivity(Authentication authentication) {

        List<AuditLog> activity = auditLogRepository.findAll().stream()
                .filter(log -> authentication.getName().equals(log.getUsername()))
                .filter(log -> "LOGIN".equals(log.getAction()) || "TOKEN_REFRESH".equals(log.getAction())
                        || "LOGOUT".equals(log.getAction()))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(20)
                .collect(Collectors.toList());

        return ResponseEntity.ok(activity);
    }
}
