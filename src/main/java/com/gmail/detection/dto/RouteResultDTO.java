package com.gmail.detection.dto;

import lombok.Data;

@Data
public class RouteResultDTO {

    private Long emailId;

    private String subject;

    private boolean spam;

    private String routedDepartment;

    private String routedTo;

    private String priority;

    private Integer confidenceScore;

    private AssignmentDTO assignment;

    private String message;
}
