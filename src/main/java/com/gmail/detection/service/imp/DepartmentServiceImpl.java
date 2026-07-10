package com.gmail.detection.service.imp;

import com.gmail.detection.entity.Department;
import com.gmail.detection.exception.DuplicateResourceException;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.DepartmentRepository;
import com.gmail.detection.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Department createDepartment(Department department) {

        if (departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new DuplicateResourceException(
                    "Department already exists : " + department.getDepartmentName());
        }

        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, Department department) {

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found with ID : " + id));

        existingDepartment.setDepartmentName(department.getDepartmentName());
        existingDepartment.setDescription(department.getDescription());

        return departmentRepository.save(existingDepartment);
    }

    @Override
    public Department getDepartmentById(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found with ID : " + id));
    }

    @Override
    public List<Department> getAllDepartments() {

        return departmentRepository.findAll();
    }

    @Override
    public void deleteDepartment(Long id) {

        Department department = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Department not found with ID : " + id));

        departmentRepository.delete(department);
    }
}