package com.gmail.detection.controller;

import com.gmail.detection.dto.EmailResponseDTO;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.EmailCategory;
import com.gmail.detection.enums.EmailStatus;
import com.gmail.detection.enums.Priority;
import com.gmail.detection.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Advanced filtering endpoints for the email list views (Priority Distribution,
 * Categories, Departments filters on the dashboard/inbox screens).
 */
@RestController
@RequestMapping("/api/filters")
@RequiredArgsConstructor
public class FilterController {

    private final EmailService emailService;

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<EmailResponseDTO>> byPriority(@PathVariable Priority priority) {
        return ResponseEntity.ok(emailService.getEmailsByPriority(priority));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<EmailResponseDTO>> byCategory(@PathVariable EmailCategory category) {
        return ResponseEntity.ok(emailService.getEmailsByCategory(category));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<EmailResponseDTO>> byDepartment(@PathVariable DepartmentType department) {
        return ResponseEntity.ok(emailService.getEmailsByDepartment(department));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmailResponseDTO>> byStatus(@PathVariable EmailStatus status) {
        return ResponseEntity.ok(emailService.getEmailsByStatus(status));
    }
}
