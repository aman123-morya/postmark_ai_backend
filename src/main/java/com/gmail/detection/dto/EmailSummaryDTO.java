package com.gmail.detection.dto;

import lombok.Data;

@Data
public class EmailSummaryDTO {

    private Long id;

    private String sender;

    private String subject;

    private String originalText;

    private String summary;

}