package com.gmail.detection.controller;

import com.gmail.detection.dto.EmailDTO;
import com.gmail.detection.dto.EmailResponseDTO;
import com.gmail.detection.service.GmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gmail")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GmailController {

    private final GmailService gmailService;

    @PostMapping("/send")
    public ResponseEntity<EmailResponseDTO> sendEmail(
            @RequestBody EmailDTO emailDTO) {

        return ResponseEntity.ok(
                gmailService.sendEmail(emailDTO));
    }

    @GetMapping("/inbox")
    public ResponseEntity<List<EmailResponseDTO>> getInbox() {

        return ResponseEntity.ok(
                gmailService.readInbox());
    }

    @GetMapping("/sent")
    public ResponseEntity<List<EmailResponseDTO>> getSentEmails() {

        return ResponseEntity.ok(
                gmailService.readSentEmails());
    }

    @GetMapping("/drafts")
    public ResponseEntity<List<EmailResponseDTO>> getDrafts() {

        return ResponseEntity.ok(
                gmailService.readDrafts());
    }

    @DeleteMapping("/{gmailMessageId}")
    public ResponseEntity<String> deleteEmail(
            @PathVariable String gmailMessageId) {

        gmailService.deleteEmail(gmailMessageId);

        return ResponseEntity.ok("Email deleted successfully.");
    }

    @PostMapping("/label")
    public ResponseEntity<String> createLabel(
            @RequestParam String labelName) {

        gmailService.createLabel(labelName);

        return ResponseEntity.ok("Label created successfully.");
    }

    @PostMapping("/apply-label")
    public ResponseEntity<String> applyLabel(
            @RequestParam String gmailMessageId,
            @RequestParam String labelName) {

        gmailService.applyLabel(gmailMessageId, labelName);

        return ResponseEntity.ok("Label applied successfully.");
    }

    @PostMapping("/forward")
    public ResponseEntity<String> forwardEmail(
            @RequestParam String gmailMessageId,
            @RequestParam String receiverEmail) {

        gmailService.forwardEmail(
                gmailMessageId,
                receiverEmail);

        return ResponseEntity.ok("Email forwarded successfully.");
    }

    @PostMapping("/sync/inbox")
    public ResponseEntity<String> syncInbox() {

        gmailService.syncInbox();

        return ResponseEntity.ok("Inbox synchronized.");
    }

    @PostMapping("/sync/sent")
    public ResponseEntity<String> syncSent() {

        gmailService.syncSentEmails();

        return ResponseEntity.ok("Sent emails synchronized.");
    }

    @PostMapping("/sync/drafts")
    public ResponseEntity<String> syncDrafts() {

        gmailService.syncDrafts();

        return ResponseEntity.ok("Drafts synchronized.");
    }

    @GetMapping("/readsent")
    public ResponseEntity<List<EmailResponseDTO>> readSentEmails() {

        return ResponseEntity.ok(gmailService.readSentEmails());

    }

}