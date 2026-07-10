package com.gmail.detection.dto;

import lombok.Data;

@Data
public class AssignmentDTO {

    private Long id;

    private String emailSubject;

    private String assignedTo;

    private String department;

    private String status;

    private String remarks;

}