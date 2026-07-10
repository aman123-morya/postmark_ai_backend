package com.gmail.detection.service.imp;

import com.gmail.detection.entity.Email;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.service.RoutingService;
import org.springframework.stereotype.Service;

@Service
public class RoutingServiceImpl implements RoutingService {

    private final EmailRepository emailRepository;

    public RoutingServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    // ==========================================
    // Detect Department using Subject & Body
    // ==========================================

    @Override
    public String detectDepartment(String subject, String body) {

        String text = (subject + " " + body).toLowerCase();

        // =========================
        // Finance Department
        // =========================
        if (text.contains("payment")
                || text.contains("invoice")
                || text.contains("refund")
                || text.contains("bank")
                || text.contains("salary")
                || text.contains("tax")
                || text.contains("statement")
        ) {

            return DepartmentType.FINANCE.name();
        }

        // =========================
        // HR Department
        // =========================
        if (text.contains("leave")
                || text.contains("employee")
                || text.contains("interview")
                || text.contains("recruitment")
                || text.contains("attendance")
                || text.contains("holiday")
                || text.contains("internship")
                || text.contains("career")
                || text.contains("hiring")
                || text.contains("cv")
                || text.contains("resume")
                || text.contains("placement")
                || text.contains("job")
        ) {

            return DepartmentType.HR.name();
        }

        // =========================
        // IT Department
        // =========================
        if (text.contains("bug")
                || text.contains("error")
                || text.contains("system")
                || text.contains("server")
                || text.contains("software")
                || text.contains("login")
                || text.contains("password")
                || text.contains("java")
                || text.contains("database")
                || text.contains("backend")
                || text.contains("frontent")
                || text.contains("github")
                || text.contains("api")
        ) {

            return DepartmentType.IT.name();
        }

        // =========================
        // Sales Department
        // =========================
        if (text.contains("order")
                || text.contains("customer")
                || text.contains("product")
                || text.contains("quotation")
                || text.contains("purchase")) {

            return DepartmentType.SALES.name();
        }

        // =========================
        // Marketing Department
        // =========================
        if (text.contains("promotion")
                || text.contains("advertisement")
                || text.contains("campaign")
                || text.contains("marketing")) {

            return DepartmentType.MARKETING.name();
        }

        // =========================
        // Default
        // =========================

        return DepartmentType.SUPPORT.name();
    }

    // ==========================================
    // Route Email Automatically
    // ==========================================

    @Override
    public String routeEmail(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        String department = detectDepartment(
                email.getSubject(),
                email.getBody());

        email.setDepartment(DepartmentType.valueOf(department));

        emailRepository.save(email);

        return department;
    }

    // ==========================================
    // Assign Department Manually
    // ==========================================

    @Override
    public boolean assignDepartment(Long emailId, String department) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        try {

            email.setDepartment(
                    DepartmentType.valueOf(department.toUpperCase()));

            emailRepository.save(email);

            return true;

        } catch (IllegalArgumentException ex) {

            return false;
        }
    }
}