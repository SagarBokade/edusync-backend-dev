package com.project.edusync.ams.model.dto.request;

import com.project.edusync.ams.model.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmsAbsenceApprovalRequest {

    @NotNull(message = "Approval status is required.")
    private ApprovalStatus approvalStatus;

    // The staff member ID who approved the submission (pulled from token).
    @NotNull(message = "Approving staff ID is required.")
    private Long approvedByStaffId;

    private String reviewerNotes;
}