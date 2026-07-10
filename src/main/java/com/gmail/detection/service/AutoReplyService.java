package com.gmail.detection.service;

import com.gmail.detection.dto.AutoReplyDTO;

public interface AutoReplyService {

    AutoReplyDTO generateReply(Long emailId);

    String generateReplyMessage(String sentiment);

}