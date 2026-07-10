package com.gmail.detection.dto;

import lombok.Data;

@Data
public class AutoReplyDTO {

    private Long emailId;

    private String receiver;

    private String subject;

    private String replyMessage;

}