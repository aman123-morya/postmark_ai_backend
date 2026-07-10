package com.gmail.detection.controller;

import com.gmail.detection.dto.JwtResponse;
import com.gmail.detection.dto.LoginRequest;
import com.gmail.detection.dto.RefreshTokenRequest;
import com.gmail.detection.dto.RegisterRequest;
import com.gmail.detection.dto.UserDTO;
import com.gmail.detection.entity.User;
import com.gmail.detection.exception.UnauthorizedException;
import com.gmail.detection.repository.UserRepository;
import com.gmail.detection.security.JwtUtil;
import com.gmail.detection.security.TokenBlacklistService;
import com.gmail.detection.service.AuditLogService;
import com.gmail.detection.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 * Public authentication endpoints: register, login, refresh, logout.
 * Both ADMIN and normal USERS authenticate through the same endpoints;
 * the JWT carries the user's role, and downstream endpoints are gated by it.
 * Every attempt (success or failure) is written to the audit log.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String MODULE = "AUTH";

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final AuditLogService auditLogService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request,
                                             HttpServletRequest httpRequest) {

        UserDTO created = userService.registerUser(request);

        auditLogService.log(request.getEmail(), "REGISTER", MODULE,
                "New account registered with role " + request.getRole(), "SUCCESS", httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            // Authentication succeeded - safe to look the user up directly for token claims.
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

            auditLogService.log(user.getEmail(), "LOGIN", MODULE, "Login successful", "SUCCESS", httpRequest);

            return ResponseEntity.ok(buildAuthResponse(user));

        } catch (BadCredentialsException ex) {
            auditLogService.log(request.getEmail(), "LOGIN", MODULE, "Invalid credentials", "FAILURE", httpRequest);
            throw new UnauthorizedException("Invalid email or password.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@Valid @RequestBody RefreshTokenRequest request,
                                                HttpServletRequest httpRequest) {

        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("The provided token is not a valid refresh token.");
        }

        if (tokenBlacklistService.isBlacklisted(refreshToken) || jwtUtil.isTokenExpired(refreshToken)) {
            throw new UnauthorizedException("Refresh token is expired or has been revoked. Please log in again.");
        }

        String email = jwtUtil.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token."));

        // Rotate: invalidate the used refresh token and issue a brand new pair.
        tokenBlacklistService.blacklist(refreshToken, jwtUtil.extractExpiration(refreshToken));

        auditLogService.log(user.getEmail(), "TOKEN_REFRESH", MODULE, "Access token refreshed", "SUCCESS", httpRequest);

        return ResponseEntity.ok(buildAuthResponse(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpRequest,
                                          @RequestBody(required = false) RefreshTokenRequest request) {

        String authHeader = httpRequest.getHeader("Authorization");
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            try {
                email = jwtUtil.extractEmail(accessToken);
            } catch (Exception ignored) {
                // Token already invalid/expired - nothing more to extract, still proceed with logout.
            }
            tokenBlacklistService.blacklist(accessToken, jwtUtil.extractExpiration(accessToken));
        }

        if (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank()) {
            String refreshToken = request.getRefreshToken();
            tokenBlacklistService.blacklist(refreshToken, jwtUtil.extractExpiration(refreshToken));
        }

        auditLogService.log(email, "LOGOUT", MODULE, "User logged out", "SUCCESS", httpRequest);

        return ResponseEntity.ok("Logged out successfully.");
    }

    private JwtResponse buildAuthResponse(User user) {

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name(), user.getId());

        return JwtResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .build();
    }
}
