package com.gmail.detection.dto;

import lombok.Data;

@Data
public class SpamRecordDTO {

    private Long id;

    private String sender;

    private String subject;

    private String reason;

    private boolean blocked;

}