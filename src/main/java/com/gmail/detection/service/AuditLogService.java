package com.gmail.detection.service;

import com.gmail.detection.entity.AuditLog;
import com.gmail.detection.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Writes entries to the audit_logs table. Kept deliberately simple (no
 * dedicated interface/impl split) since it's a single straightforward
 * responsibility used by several controllers.
 */
@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void log(String username, String action, String module, String description,
                     String status, HttpServletRequest request) {

        AuditLog auditLog = AuditLog.builder()
                .username(username != null ? username : "anonymous")
                .action(action)
                .module(module)
                .description(description)
                .status(status)
                .ipAddress(clientIp(request))
                .build();

        auditLogRepository.save(auditLog);
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
