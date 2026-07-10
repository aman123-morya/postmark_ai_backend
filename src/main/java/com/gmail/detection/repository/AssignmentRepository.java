package com.gmail.detection.repository;

import com.gmail.detection.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByDepartment(String department);

    List<Assignment> findByAssignedTo(String assignedTo);

    List<Assignment> findByStatus(String status);

}