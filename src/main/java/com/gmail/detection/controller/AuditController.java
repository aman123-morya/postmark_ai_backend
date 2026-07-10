package com.gmail.detection.controller;

import com.gmail.detection.entity.AuditLog;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Read-only admin view of audit logs. Access to /api/audit/** is restricted to
 * ROLE_ADMIN in SecurityConfig.
 *
 * Note: no dedicated AuditService exists yet - this reads directly from the
 * repository, matching the read-only nature of this endpoint. If write/query
 * logic grows beyond simple lookups, promote this into a proper service.
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getLogById(@PathVariable Long id) {
        AuditLog log = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found with ID : " + id));
        return ResponseEntity.ok(log);
    }
}
