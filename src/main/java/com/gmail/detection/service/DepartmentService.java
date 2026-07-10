package com.gmail.detection.service;

import com.gmail.detection.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department createDepartment(Department department);

    Department updateDepartment(Long id, Department department);

    Department getDepartmentById(Long id);

    List<Department> getAllDepartments();

    void deleteDepartment(Long id);

}