package com.gmail.detection.service.imp;
import com.gmail.detection.dto.AssignmentDTO;
import com.gmail.detection.entity.Assignment;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.AssignmentRepository;
import com.gmail.detection.service.AssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public AssignmentDTO assignEmail(AssignmentDTO assignmentDTO) {

        Assignment assignment = convertToEntity(assignmentDTO);

        Assignment savedAssignment = assignmentRepository.save(assignment);

        return convertToDTO(savedAssignment);
    }

    @Override
    public AssignmentDTO updateAssignment(Long id, AssignmentDTO assignmentDTO) {

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assignment not found with ID : " + id));

        assignment.setEmailSubject(assignmentDTO.getEmailSubject());
        assignment.setAssignedTo(assignmentDTO.getAssignedTo());
        assignment.setDepartment(assignmentDTO.getDepartment());
        assignment.setStatus(assignmentDTO.getStatus());
        assignment.setRemarks(assignmentDTO.getRemarks());

        Assignment updatedAssignment = assignmentRepository.save(assignment);

        return convertToDTO(updatedAssignment);
    }

    @Override
    public AssignmentDTO getAssignmentById(Long id) {

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assignment not found with ID : " + id));

        return convertToDTO(assignment);
    }

    @Override
    public List<AssignmentDTO> getAllAssignments() {

        return assignmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByDepartment(String department) {

        return assignmentRepository.findByDepartment(department)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByEmployee(String employee) {

        return assignmentRepository.findByAssignedTo(employee)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAssignment(Long id) {

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Assignment not found with ID : " + id));

        assignmentRepository.delete(assignment);
    }

    // ==========================
    // Entity -> DTO
    // ==========================

    private AssignmentDTO convertToDTO(Assignment assignment) {

        AssignmentDTO dto = new AssignmentDTO();

        dto.setId(assignment.getId());
        dto.setEmailSubject(assignment.getEmailSubject());
        dto.setAssignedTo(assignment.getAssignedTo());
        dto.setDepartment(assignment.getDepartment());
        dto.setStatus(assignment.getStatus());
        dto.setRemarks(assignment.getRemarks());

        return dto;
    }

    // ==========================
    // DTO -> Entity
    // ==========================

    private Assignment convertToEntity(AssignmentDTO dto) {

        Assignment assignment = new Assignment();

        assignment.setEmailSubject(dto.getEmailSubject());
        assignment.setAssignedTo(dto.getAssignedTo());
        assignment.setDepartment(dto.getDepartment());
        assignment.setStatus(dto.getStatus());
        assignment.setRemarks(dto.getRemarks());

        return assignment;
    }
}