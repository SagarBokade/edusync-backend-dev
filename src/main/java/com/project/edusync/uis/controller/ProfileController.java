package com.project.edusync.uis.controller;

import com.project.edusync.common.security.AuthUtil;
import com.project.edusync.uis.model.dto.profile.ComprehensiveUserProfileResponseDTO;
import com.project.edusync.uis.model.dto.profile.UserProfileDTO;
import com.project.edusync.uis.model.dto.profile.UserProfileUpdateDTO;
import com.project.edusync.uis.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing User Profiles.
 * <p>
 * This controller serves two main audiences:
 * 1. The authenticated user managing their own profile (/me endpoints).
 * 2. Administrators managing other users' profiles (/{userId} endpoints).
 * </p>
 */
@RestController
@RequestMapping("${api.url}/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "Endpoints for viewing and updating user profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final AuthUtil authUtil;

    // =================================================================================
    // SELF-SERVICE ENDPOINTS (/me)
    // =================================================================================

    /**
     * Get the currently logged-in user's comprehensive profile.
     * <p>
     * This returns the "Full Picture" — not just the name/email, but also
     * their specific role details (e.g., if they are a Student, it returns
     * student details; if a Teacher, teacher details).
     * </p>
     *
     * @return The full profile hierarchy for the current user.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('profile:read:own')")
    @Operation(summary = "Get My Profile", description = "Retrieves the full profile details (Personal + Role Specific) of the currently logged-in user.")
    public ResponseEntity<ComprehensiveUserProfileResponseDTO> getMyProfile() {
        // We use AuthUtil to safely extract the ID from the SecurityContext (JWT)
        Long currentUserId = authUtil.getCurrentUserId();
        return ResponseEntity.ok(profileService.getProfileByUserId(currentUserId));
    }

    /**
     * Update the currently logged-in user's basic profile information.
     * <p>
     * Allows users to update fields like Bio, Preferred Name, etc.
     * It does NOT allow updating sensitive system fields (like Role or Staff ID).
     * </p>
     *
     * @param updateDto The subset of fields the user is allowed to change.
     * @return The updated profile state.
     */
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('profile:update:own')")
    @Operation(summary = "Update My Profile", description = "Updates editable personal details (Bio, Preferred Name, etc.) for the current user.")
    public ResponseEntity<UserProfileDTO> updateMyProfile(@Valid @RequestBody UserProfileUpdateDTO updateDto) {
        Long currentUserId = authUtil.getCurrentUserId();
        UserProfileDTO updatedProfile = profileService.updateProfileByUserId(currentUserId, updateDto);
        return ResponseEntity.ok(updatedProfile);
    }

    // =================================================================================
    // ADMINISTRATIVE ENDPOINTS (/{userId})
    // =================================================================================

    /**
     * Get ANY user's profile by their User ID.
     * <p>
     * Restricted to Admins/Staff with 'profile:read:all' permission.
     * Used by School Admins to view details of any student, parent, or staff member.
     * </p>
     *
     * @param userId The target user's ID.
     * @return The target user's comprehensive profile.
     */
    @GetMapping("/{userId}")
//    @PreAuthorize("hasAuthority('profile:read:all')")
    @Operation(summary = "Get User Profile (Admin)", description = "Retrieves the full profile of any user. Requires administrative privileges.")
    public ResponseEntity<ComprehensiveUserProfileResponseDTO> getProfileByUserId(@PathVariable Long userId) {
        // Pass the requested userId directly to the service
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }

    /**
     * Update ANY user's profile by their User ID.
     * <p>
     * Restricted to Admins with 'profile:update:all' permission.
     * Useful for fixing typos in names, updating incorrect birth dates, etc.
     * </p>
     *
     * @param userId    The target user's ID.
     * @param updateDto The new data.
     * @return The updated profile.
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('profile:update:all')")
    @Operation(summary = "Update User Profile (Admin)", description = "Updates personal details for a specific user. Requires administrative privileges.")
    public ResponseEntity<UserProfileDTO> updateProfileByUserId(
            @PathVariable Long userId,
            @Valid @RequestBody UserProfileUpdateDTO updateDto) {

        UserProfileDTO updatedProfile = profileService.updateProfileByUserId(userId, updateDto);
        return ResponseEntity.ok(updatedProfile);
    }
}