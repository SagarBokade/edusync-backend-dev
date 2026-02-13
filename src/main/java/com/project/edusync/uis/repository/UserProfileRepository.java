package com.project.edusync.uis.repository;

import com.project.edusync.iam.model.entity.User;
import com.project.edusync.uis.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
    /** Finds a UserProfile based on the associated User entity. */
    Optional<UserProfile> findByUser(User user);

    /**
     * Finds a UserProfile entity by its associated User's ID.
     * This is the primary way we'll fetch a profile for a logged-in user.
     */
    Optional<UserProfile> findByUser_Id(Long userId);
}
