package com.gmail.detection.controller;

import com.gmail.detection.dto.UserDTO;
import com.gmail.detection.dto.UserStatusRequest;
import com.gmail.detection.service.AuditLogService;
import com.gmail.detection.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only user management. Access to /api/users/** is restricted to ROLE_ADMIN
 * in SecurityConfig.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserControler {

    private final UserService userService;
    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO,
                                               Authentication authentication, HttpServletRequest httpRequest) {

        UserDTO updated = userService.updateUser(id, userDTO);

        auditLogService.log(authentication.getName(), "UPDATE_USER", "USER_MANAGEMENT",
                "Updated user ID " + id, "SUCCESS", httpRequest);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id,
                                              Authentication authentication, HttpServletRequest httpRequest) {

        userService.deleteUser(id);

        auditLogService.log(authentication.getName(), "DELETE_USER", "USER_MANAGEMENT",
                "Deleted user ID " + id, "SUCCESS", httpRequest);

        return ResponseEntity.ok("User deleted successfully.");
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    // Explicit block/activate action, separate from the general update endpoint
    // so the frontend can offer a single-click toggle without resending the
    // whole user form.
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> updateStatus(@PathVariable Long id, @RequestBody UserStatusRequest request,
                                                 Authentication authentication, HttpServletRequest httpRequest) {

        UserDTO updated = userService.updateUserStatus(id, request.isActive());

        auditLogService.log(authentication.getName(), request.isActive() ? "ACTIVATE_USER" : "BLOCK_USER",
                "USER_MANAGEMENT", (request.isActive() ? "Activated" : "Blocked") + " user ID " + id,
                "SUCCESS", httpRequest);

        return ResponseEntity.ok(updated);
    }
}
