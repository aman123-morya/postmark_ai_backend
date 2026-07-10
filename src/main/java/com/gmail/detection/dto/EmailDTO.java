package com.gmail.detection.dto;

import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.EmailCategory;
import com.gmail.detection.enums.Priority;
import lombok.Data;

@Data
public class EmailDTO {

    private String gmailMessageId;

    private String sender;

    private String receiver;

    private String subject;

    private String body;

    private EmailCategory category;

    private Priority priority;

    private DepartmentType department;

}