package com.gmail.detection.repository;

import com.gmail.detection.entity.User;
import com.gmail.detection.enums.DepartmentType;
import com.gmail.detection.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByDepartment(DepartmentType department);

    List<User> findByRole(Role role);

    boolean existsByEmail(String email);
}