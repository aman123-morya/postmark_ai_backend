package com.gmail.detection.service.imp;

import com.gmail.detection.dto.ChangePasswordRequest;
import com.gmail.detection.dto.ProfileUpdateRequest;
import com.gmail.detection.dto.UserDTO;
import com.gmail.detection.entity.User;
import com.gmail.detection.exception.BadRequestException;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.UserRepository;
import com.gmail.detection.service.ProfileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public ProfileServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO getMyProfile(String email) {
        return mapToDTO(findUser(email));
    }

    @Override
    public UserDTO updateMyProfile(String email, ProfileUpdateRequest request) {

        User user = findUser(email);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDepartment(request.getDepartment());

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {

        User user = findUser(email);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDTO uploadAvatar(String email, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No file uploaded.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed for avatars.");
        }

        User user = findUser(email);

        try {
            Path avatarDir = Paths.get(uploadDir, "avatars");
            Files.createDirectories(avatarDir);

            String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "avatar";
            String extension = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf('.'))
                    : "";
            String filename = "user-" + user.getId() + "-" + UUID.randomUUID() + extension;

            Path destination = avatarDir.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            user.setAvatarUrl("/uploads/avatars/" + filename);
            userRepository.save(user);

        } catch (IOException ex) {
            throw new BadRequestException("Could not store avatar file: " + ex.getMessage());
        }

        return mapToDTO(user);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email : " + email));
    }

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
