package com.gmail.detection.service;

import com.gmail.detection.dto.AssignmentDTO;

import java.util.List;

public interface AssignmentService {

    AssignmentDTO assignEmail(AssignmentDTO assignmentDTO);

    AssignmentDTO updateAssignment(Long id, AssignmentDTO assignmentDTO);

    AssignmentDTO getAssignmentById(Long id);

    List<AssignmentDTO> getAllAssignments();

    List<AssignmentDTO> getAssignmentsByDepartment(String department);

    List<AssignmentDTO> getAssignmentsByEmployee(String employee);

    void deleteAssignment(Long id);

}