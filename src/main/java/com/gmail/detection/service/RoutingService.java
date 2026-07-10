package com.gmail.detection.service;

public interface RoutingService {

    String detectDepartment(String emailSubject, String emailBody);

    String routeEmail(Long emailId);

    boolean assignDepartment(Long emailId, String department);

}