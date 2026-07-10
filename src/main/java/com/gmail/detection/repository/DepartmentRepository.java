package com.gmail.detection.repository;

import com.gmail.detection.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentName(String departmentName);

    boolean existsByDepartmentName(String departmentName);

}