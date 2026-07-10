package com.gmail.detection.controller;

import com.gmail.detection.dto.EmailDTO;
import com.gmail.detection.dto.EmailResponseDTO;
import com.gmail.detection.dto.EmailUpdateRequest;
import com.gmail.detection.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages the locally-persisted email records (as opposed to GmailController,
 * which talks directly to the Gmail API). This is the source of truth used by
 * the dashboard, AI features, spam detection, and assignment workflows.
 */
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<EmailResponseDTO> sendEmail(@RequestBody EmailDTO emailDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailService.sendEmail(emailDTO));
    }

    @PostMapping("/draft")
    public ResponseEntity<EmailResponseDTO> saveDraft(@RequestBody EmailDTO emailDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailService.saveEmail(emailDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailResponseDTO> getEmailById(@PathVariable Long id) {
        return ResponseEntity.ok(emailService.getEmailById(id));
    }

    @GetMapping
    public ResponseEntity<List<EmailResponseDTO>> getAllEmails() {
        return ResponseEntity.ok(emailService.getAllEmails());
    }

    @GetMapping("/sender/{sender}")
    public ResponseEntity<List<EmailResponseDTO>> getEmailsBySender(@PathVariable String sender) {
        return ResponseEntity.ok(emailService.getEmailsBySender(sender));
    }

    @GetMapping("/receiver/{receiver}")
    public ResponseEntity<List<EmailResponseDTO>> getEmailsByReceiver(@PathVariable String receiver) {
        return ResponseEntity.ok(emailService.getEmailsByReceiver(receiver));
    }

    @GetMapping("/spam")
    public ResponseEntity<List<EmailResponseDTO>> getSpamEmails() {
        return ResponseEntity.ok(emailService.getSpamEmails());
    }

    @GetMapping("/starred")
    public ResponseEntity<List<EmailResponseDTO>> getStarredEmails() {
        return ResponseEntity.ok(emailService.getStarredEmails());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmail(@PathVariable Long id) {
        emailService.deleteEmail(id);
        return ResponseEntity.ok("Email deleted successfully.");
    }

    // Star / archive / workflow-status toggles - the read/list endpoints
    // above are all read-only, so inbox actions like starring or archiving
    // need this one small mutable-flags endpoint.
    @PatchMapping("/{id}")
    public ResponseEntity<EmailResponseDTO> updateEmail(@PathVariable Long id, @RequestBody EmailUpdateRequest request) {
        return ResponseEntity.ok(emailService.updateEmail(id, request));
    }
}
