package com.gmail.detection.service.imp;

import com.gmail.detection.dto.*;
import com.gmail.detection.entity.Assignment;
import com.gmail.detection.entity.Email;
import com.gmail.detection.entity.SpamRecord;
import com.gmail.detection.entity.User;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.EmailStatus;
import com.gmail.detection.enums.Role;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.AssignmentRepository;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.repository.SpamRecordRepository;
import com.gmail.detection.repository.UserRepository;
import com.gmail.detection.service.AIService;
import com.gmail.detection.service.EmailRoutingService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailRoutingServiceImpl implements EmailRoutingService {

    private final EmailRepository emailRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final SpamRecordRepository spamRecordRepository;
    private final AIService aiService;

    private static final Set<String> OPEN_STATUSES = Set.of("PENDING", "IN_PROGRESS", "OPEN");

    public EmailRoutingServiceImpl(EmailRepository emailRepository,
                                    AssignmentRepository assignmentRepository,
                                    UserRepository userRepository,
                                    SpamRecordRepository spamRecordRepository,
                                    AIService aiService) {
        this.emailRepository = emailRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.spamRecordRepository = spamRecordRepository;
        this.aiService = aiService;
    }

    @Override
    public RouteResultDTO autoRoute(Long emailId) {

        // analyzeCompleteEmail() (see AIServiceImpl) both classifies the
        // email via Gemini AND persists the result onto the Email row.
        AIResponseDTO analysis = aiService.analyzeCompleteEmail(emailId);

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() -> new ResourceNotFoundException("Email not found with ID : " + emailId));

        RouteResultDTO result = new RouteResultDTO();
        result.setEmailId(email.getId());
        result.setSubject(email.getSubject());
        result.setPriority(email.getPriority() != null ? email.getPriority().name() : null);
        result.setConfidenceScore(email.getConfidenceScore());

        boolean isSpam = Boolean.TRUE.equals(analysis.getSpam());
        result.setSpam(isSpam);

        if (isSpam) {
            SpamRecord record = new SpamRecord();
            record.setSender(email.getSender());
            record.setSubject(email.getSubject());
            record.setReason(email.getReason() != null ? email.getReason() : "Flagged as spam by AI classification");
            record.setBlocked(true);
            spamRecordRepository.save(record);

            email.setArchived(true);
            emailRepository.save(email);

            result.setMessage("Flagged as spam and archived - no assignment created.");
            return result;
        }

        DepartmentType department = email.getDepartment() != null ? email.getDepartment() : DepartmentType.GENERAL;
        String assignee = pickLeastLoadedAssignee(department);

        Assignment assignment = Assignment.builder()
                .emailSubject(email.getSubject())
                .assignedTo(assignee)
                .department(department.name())
                .status("PENDING")
                .remarks("Auto-routed by AI" +
                        (email.getConfidenceScore() != null ? " · confidence " + email.getConfidenceScore() + "%" : "") +
                        (email.getPriority() != null ? " · priority " + email.getPriority() : ""))
                .build();

        assignmentRepository.save(assignment);

        if (email.getStatus() == null || email.getStatus() == EmailStatus.RECEIVED) {
            email.setStatus(EmailStatus.OPEN);
        }
        emailRepository.save(email);

        result.setRoutedDepartment(department.name());
        result.setRoutedTo(assignee);
        result.setAssignment(toAssignmentDTO(assignment));
        result.setMessage("Routed to " + department.name() + " (" + assignee + ").");

        return result;
    }

    @Override
    public AutoRouteSummaryDTO autoRouteAll() {

        List<Email> pending = emailRepository.findByStatus(EmailStatus.RECEIVED);

        List<RouteResultDTO> results = new ArrayList<>();
        int spamCaught = 0;
        Map<String, Long> byDepartment = new LinkedHashMap<>();

        for (Email email : pending) {
            RouteResultDTO result = autoRoute(email.getId());
            results.add(result);

            if (result.isSpam()) {
                spamCaught++;
            } else if (result.getRoutedDepartment() != null) {
                byDepartment.merge(result.getRoutedDepartment(), 1L, Long::sum);
            }
        }

        AutoRouteSummaryDTO summary = new AutoRouteSummaryDTO();
        summary.setProcessed(results.size());
        summary.setSpamCaught(spamCaught);
        summary.setRoutedByDepartment(byDepartment);
        summary.setResults(results);

        return summary;
    }

    // Picks whichever active MANAGER/EMPLOYEE in the target department
    // currently has the fewest open (non-resolved/closed) assignments, so
    // routing balances load instead of always hitting the same person.
    // Falls back to any ADMIN, then to a department-labelled placeholder
    // if literally nobody is registered in that department yet.
    private String pickLeastLoadedAssignee(DepartmentType department) {

        List<User> candidates = userRepository.findByDepartment(department).stream()
                .filter(User::isActive)
                .filter(u -> u.getRole() == Role.EMPLOYEE || u.getRole() == Role.MANAGER)
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = userRepository.findByRole(Role.ADMIN).stream()
                    .filter(User::isActive)
                    .collect(Collectors.toList());
        }

        if (candidates.isEmpty()) {
            return "unassigned-" + department.name().toLowerCase();
        }

        Map<String, Long> openLoadByEmail = new HashMap<>();
        for (User u : candidates) {
            long openCount = assignmentRepository.findByAssignedTo(u.getEmail()).stream()
                    .filter(a -> OPEN_STATUSES.contains(a.getStatus()))
                    .count();
            openLoadByEmail.put(u.getEmail(), openCount);
        }

        return candidates.stream()
                .min(Comparator.comparingLong(u -> openLoadByEmail.getOrDefault(u.getEmail(), 0L)))
                .map(User::getEmail)
                .orElse(candidates.get(0).getEmail());
    }

    private AssignmentDTO toAssignmentDTO(Assignment a) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(a.getId());
        dto.setEmailSubject(a.getEmailSubject());
        dto.setAssignedTo(a.getAssignedTo());
        dto.setDepartment(a.getDepartment());
        dto.setStatus(a.getStatus());
        dto.setRemarks(a.getRemarks());
        return dto;
    }
}
