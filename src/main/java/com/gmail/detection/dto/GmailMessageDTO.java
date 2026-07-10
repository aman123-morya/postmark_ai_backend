package com.gmail.detection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GmailMessageDTO {

    private String gmailId;

    private String from;

    private String to;

    private String subject;

    private String snippet;

    private String body;

}