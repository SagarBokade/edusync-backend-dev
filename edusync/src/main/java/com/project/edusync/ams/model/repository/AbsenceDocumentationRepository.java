package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.AbsenceDocumentation;
import com.project.edusync.ams.model.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbsenceDocumentationRepository extends JpaRepository<AbsenceDocumentation, Long> {

    /**
     * Retrieves all absence documentation records that require review (PENDING).
     * This powers the 'Absence Review Queue' for administrative staff.
     */
    Page<AbsenceDocumentation> findByApprovalStatusOrderByCreatedAtAsc(
            ApprovalStatus approvalStatus,
            Pageable pageable);

    /**
     * Finds documentation submitted by a specific user (e.g., a parent/guardian).
     */
    Page<AbsenceDocumentation> findBySubmittedByUserIdOrderByCreatedAtDesc(
            Long submittedByUserId,
            Pageable pageable);

    /**
     * Finds documentation approved by a specific staff member.
     */
    Page<AbsenceDocumentation> findByApprovedByStaffIdOrderByUpdatedAtDesc(
            Long approvedByStaffId,
            Pageable pageable);

    /**
     * Since the PK is shared, we can use the dailyAttendanceId to find the documentation.
     * This is useful for checking if an excuse has been submitted for a specific absence.
     */
//    Optional<AbsenceDocumentation> findById(Long dailyAttendanceId);
}