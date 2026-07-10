package com.gmail.detection.controller;

import com.gmail.detection.dto.AssignmentDTO;
import com.gmail.detection.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentDTO> assignEmail(@RequestBody AssignmentDTO assignmentDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentService.assignEmail(assignmentDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDTO> updateAssignment(@PathVariable Long id, @RequestBody AssignmentDTO assignmentDTO) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, assignmentDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.getAssignmentById(id));
    }

    @GetMapping
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<AssignmentDTO>> getByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByDepartment(department));
    }

    @GetMapping("/employee/{employee}")
    public ResponseEntity<List<AssignmentDTO>> getByEmployee(@PathVariable String employee) {
        return ResponseEntity.ok(assignmentService.getAssignmentsByEmployee(employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok("Assignment deleted successfully.");
    }
}
