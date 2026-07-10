package com.gmail.detection.service;

import com.gmail.detection.dto.RegisterRequest;
import com.gmail.detection.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO registerUser(RegisterRequest request);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    boolean existsByEmail(String email);

    UserDTO updateUserStatus(Long id, boolean active);

}