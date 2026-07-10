package com.gmail.detection.service.imp;

import com.gmail.detection.dto.RegisterRequest;
import com.gmail.detection.dto.UserDTO;
import com.gmail.detection.entity.User;
import com.gmail.detection.enums.OtpPurpose;
import com.gmail.detection.exception.DuplicateResourceException;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.UserRepository;
import com.gmail.detection.service.OtpService;
import com.gmail.detection.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    @Override
    public UserDTO registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists.");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        // Never store raw passwords - encode with BCrypt before persisting.
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setDepartment(request.getDepartment());
        user.setActive(true);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        // Fire off an email verification OTP. Best-effort: if SMTP isn't
        // configured this just logs the code instead of failing registration.
        otpService.generateAndSend(savedUser.getEmail(), OtpPurpose.EMAIL_VERIFICATION, "Email Verification");

        return mapToDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID : " + id));

        return mapToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID : " + id));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setRole(userDTO.getRole());
        user.setDepartment(userDTO.getDepartment());
        user.setActive(userDTO.isActive());

        User updatedUser = userRepository.save(user);

        return mapToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID : " + id));

        userRepository.delete(user);
    }

    @Override
    public boolean existsByEmail(String email) {

        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDTO updateUserStatus(Long id, boolean active) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with ID : " + id));

        user.setActive(active);

        return mapToDTO(userRepository.save(user));
    }

    // =========================
    // Entity -> DTO
    // =========================

    private UserDTO mapToDTO(User user) {

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setDepartment(user.getDepartment());
        dto.setActive(user.isActive());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setAvatarUrl(user.getAvatarUrl());

        return dto;
    }

}