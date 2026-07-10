package com.gmail.detection.controller;

import com.gmail.detection.dto.SpamRecordDTO;
import com.gmail.detection.service.SpamDetectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spam")
@RequiredArgsConstructor
public class SpamController {

    private final SpamDetectionService spamDetectionService;

    @PostMapping("/detect/{emailId}")
    public ResponseEntity<SpamRecordDTO> detectSpam(@PathVariable Long emailId) {
        return ResponseEntity.ok(spamDetectionService.detectSpam(emailId));
    }

    @GetMapping
    public ResponseEntity<List<SpamRecordDTO>> getAllSpamRecords() {
        return ResponseEntity.ok(spamDetectionService.getAllSpamRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpamRecordDTO> getSpamRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(spamDetectionService.getSpamRecordById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpamRecord(@PathVariable Long id) {
        spamDetectionService.deleteSpamRecord(id);
        return ResponseEntity.ok("Spam record deleted successfully.");
    }
}
