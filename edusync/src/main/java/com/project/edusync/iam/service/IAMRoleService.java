package com.project.edusync.iam.service;

import java.util.Set;

public interface IAMRoleService {
    /**
     * Retrieves all unique role names assigned to a specific user ID.
     * This is used by other modules (like UIS) to determine user permissions
     * and specialized profile fetching logic.
     *
     * @param userId The ID of the User from the Users table.
     * @return A Set of unique role names (e.g., "STUDENT", "TEACHER", "GUARDIAN").
     */
    Set<String> getRoleNamesByUserId(Long userId);
}