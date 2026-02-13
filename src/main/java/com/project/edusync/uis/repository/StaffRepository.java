package com.project.edusync.uis.repository;

import com.project.edusync.uis.model.entity.Staff;
import com.project.edusync.uis.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface StaffRepository extends JpaRepository<Staff,Long> {
    boolean existsByEmployeeId(String employeeId);

    Optional<Staff> findByUserProfile(UserProfile userProfile);
}
