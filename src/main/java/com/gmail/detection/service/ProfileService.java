package com.gmail.detection.service;

import com.gmail.detection.dto.ChangePasswordRequest;
import com.gmail.detection.dto.ProfileUpdateRequest;
import com.gmail.detection.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    UserDTO getMyProfile(String email);

    UserDTO updateMyProfile(String email, ProfileUpdateRequest request);

    void changePassword(String email, ChangePasswordRequest request);

    UserDTO uploadAvatar(String email, MultipartFile file);
}
