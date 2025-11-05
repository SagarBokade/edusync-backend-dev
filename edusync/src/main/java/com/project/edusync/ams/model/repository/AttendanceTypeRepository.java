package com.project.edusync.ams.repository;

import com.project.edusync.ams.model.entity.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttendanceTypeRepository extends JpaRepository<AttendanceType, Long> {

    /**
     * Finds an AttendanceType by its unique short code (e.g., "P", "A").
     */
    Optional<AttendanceType> findByShortCode(String shortCode);
}