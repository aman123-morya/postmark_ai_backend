package com.gmail.detection.service;

public interface PriorityService {

    String detectPriority(Long emailId);

    String detectPriority(String subject, String body);

    void updatePriority(Long emailId);

}