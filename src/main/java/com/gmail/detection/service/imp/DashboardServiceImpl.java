package com.gmail.detection.service.imp;

import com.gmail.detection.dto.DashboardDTO;
import com.gmail.detection.repository.AssignmentRepository;
import com.gmail.detection.repository.DepartmentRepository;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.repository.UserRepository;
import com.gmail.detection.service.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    private final DepartmentRepository departmentRepository;
    private final AssignmentRepository assignmentRepository;

    public DashboardServiceImpl(UserRepository userRepository,
                                EmailRepository emailRepository,
                                DepartmentRepository departmentRepository,
                                AssignmentRepository assignmentRepository) {

        this.userRepository = userRepository;
        this.emailRepository = emailRepository;
        this.departmentRepository = departmentRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public DashboardDTO getDashboardData() {

        DashboardDTO dashboard = new DashboardDTO();

        dashboard.setTotalUsers(userRepository.count());

        dashboard.setTotalEmails(emailRepository.count());

        dashboard.setTotalDepartments(departmentRepository.count());

        dashboard.setTotalAssignments(assignmentRepository.count());

        dashboard.setSpamEmails(
                emailRepository.findBySpam(true).size());

        dashboard.setStarredEmails(
                emailRepository.findByStarred(true).size());

        dashboard.setArchivedEmails(
                emailRepository.findAll()
                        .stream()
                        .filter(email -> email.isArchived())
                        .count());

        return dashboard;
    }
}