package com.gmail.detection.dto;

import lombok.Data;

@Data
public class DashboardDTO {

    private long totalUsers;

    private long totalEmails;

    private long totalDepartments;

    private long totalAssignments;

    private long spamEmails;

    private long starredEmails;

    private long archivedEmails;

}