package com.project.edusync.uis.service;

import com.project.edusync.uis.model.dto.profile.ComprehensiveUserProfileResponseDTO;
import com.project.edusync.uis.model.dto.profile.UserProfileDTO;
import com.project.edusync.uis.model.dto.profile.UserProfileUpdateDTO;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {
    /**
     * Retrieves a user's profile by their authentication user_id.
     * @param userId The ID of the User (from the 'iam' module)
     * @return The complete UserProfileDTO
     */
    ComprehensiveUserProfileResponseDTO getProfileByUserId(Long userId);

    /**
     * Updates a user's profile by their authentication user_id.
     * @param userId The ID of the User (from the 'iam' module)
     * @param updateDto The DTO containing fields to update
     * @return The updated UserProfileDTO
     */
    UserProfileDTO updateProfileByUserId(Long userId, UserProfileUpdateDTO updateDto);
}
