package com.gmail.detection.controller;

import com.gmail.detection.entity.Department;
import com.gmail.detection.service.AuditLogService;
import com.gmail.detection.service.DepartmentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only department management. Access to /api/departments/** is restricted
 * to ROLE_ADMIN in SecurityConfig.
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department,
                                                        Authentication authentication, HttpServletRequest httpRequest) {

        Department created = departmentService.createDepartment(department);

        auditLogService.log(authentication.getName(), "CREATE_DEPARTMENT", "DEPARTMENT_MANAGEMENT",
                "Created department: " + created.getDepartmentName(), "SUCCESS", httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id,
                                                    Authentication authentication, HttpServletRequest httpRequest) {

        departmentService.deleteDepartment(id);

        auditLogService.log(authentication.getName(), "DELETE_DEPARTMENT", "DEPARTMENT_MANAGEMENT",
                "Deleted department ID " + id, "SUCCESS", httpRequest);

        return ResponseEntity.ok("Department deleted successfully.");
    }
}
