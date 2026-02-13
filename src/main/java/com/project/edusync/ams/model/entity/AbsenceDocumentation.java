package com.project.edusync.ams.model.entity;

import com.project.edusync.ams.model.enums.ApprovalStatus;
import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Persistence entity for absence / excuse documentation.
 *
 * Note:
 *  - This entity uses AuditableEntity for createdAt/createdBy/updatedAt metadata.
 *  - documentUrl is a pointer to a file (S3/local storage/etc.). File upload is not implemented here.
 */
@Entity
@Table(name = "absence_documentation",
        indexes = {
                @Index(name = "idx_abs_doc_attendance", columnList = "attendance_id"),
                @Index(name = "idx_abs_doc_status", columnList = "approval_status")
        })
@Getter
@Setter
@NoArgsConstructor
public class AbsenceDocumentation extends AuditableEntity {

    // id, uuid and audit fields are inherited from AuditableEntity

    /**
     * Link to the original StudentDailyAttendance record this documentation refers to.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_id", nullable = false)
    private StudentDailyAttendance attendance;

    /**
     * The user (parent/student) who submitted this documentation. Logical FK to UIS.User.id.
     */
    @Column(name = "submitted_by_user_id", nullable = false)
    private Long submittedByUserId;

    /**
     * Pointer to external storage (S3, CDN, file-server).
     * Keep long length for URL storage (1000).
     */
    @Column(name = "documentation_url", length = 1000)
    private String documentationUrl;

    /**
     * Free-form text reason/explanation provided by submitter.
     */
    @Column(name = "reason_text", columnDefinition = "TEXT")
    private String reasonText;

    /**
     * The current approval status in the workflow.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", length = 20, nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    /**
     * Optional notes from reviewer (approver) - can include rejection reason or comments for approval.
     */
    @Column(name = "reviewer_notes", columnDefinition = "TEXT")
    private String reviewerNotes;

    /**
     * Staff id who made the decision (approve/reject).
     */
    @Column(name = "approved_by_staff_id")
    private Long approvedByStaffId;

    /**
     * When the decision (approve/reject) was made.
     */
    @Column(name = "decision_at")
    private LocalDateTime decisionAt;
}
