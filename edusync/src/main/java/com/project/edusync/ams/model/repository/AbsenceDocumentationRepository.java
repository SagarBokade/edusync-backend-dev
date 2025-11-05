package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.AbsenceDocumentation;
import com.project.edusync.ams.model.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AbsenceDocumentationRepository extends JpaRepository<AbsenceDocumentation, Long> {

    /**
     * Finds documentation by the dailyAttendanceId.
     * Note: Since dailyAttendanceId is the PK, this is functionally the same as findById.
     */
    Optional<AbsenceDocumentation> findByDailyAttendanceId(Long dailyAttendanceId);

    /**
     * Finds all submissions that are currently PENDING for administrator review.
     */
    List<AbsenceDocumentation> findByApprovalStatus(ApprovalStatus approvalStatus);

    /**
     * Finds all documentation submitted by a specific parent/guardian user ID.
     */
    List<AbsenceDocumentation> findBySubmittedByUserId(Long submittedByUserId);
}