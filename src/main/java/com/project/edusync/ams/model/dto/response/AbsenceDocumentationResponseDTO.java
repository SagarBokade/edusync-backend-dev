package com.project.edusync.ams.model.dto.response;

import com.project.edusync.ams.model.enums.ApprovalStatus;
import lombok.Value;
import java.time.LocalDateTime;

/**
 * Full DTO for viewing or reporting on a specific absence documentation record.
 */
@Value
public class AbsenceDocumentationResponseDTO {

    Long dailyAttendanceId; // Same as documentationId
    String uuid;

    String reasonText;
    String documentationUrl;

    ApprovalStatus approvalStatus;
    String reviewerNotes;

    /** Logical FK to UIS.User.id - The submitter */
    Long submittedByUserId;
    String submittedByUserName; // Denormalized name

    /** Logical FK to UIS.Staff.id - The approver */
    Long approvedByStaffId;
    String approvedByStaffName; // Denormalized name

    LocalDateTime createdAt;
}