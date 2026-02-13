package com.project.edusync.ams.model.dto.request;

import com.project.edusync.ams.model.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * DTO used by administrative staff to approve or reject absence documentation.
 */
@Value
public class AbsenceApprovalRequestDTO {

    /**
     * Must be APPROVED or REJECTED. PENDING status is not accepted here.
     */
    @NotNull(message = "Approval status must be specified (APPROVED or REJECTED).")
    ApprovalStatus approvalStatus;

    /**
     * Logical FK to UIS.Staff.id - the staff member performing the review.
     */
    @NotNull(message = "Approved By Staff ID is required for audit.")
    Long approvedByStaffId;

    @Size(max = 1000, message = "Reviewer notes cannot exceed 1000 characters.")
    String reviewerNotes;
}