package com.project.edusync.ams.model.dto.response;

import com.project.edusync.ams.model.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmsAbsenceDocumentationResponse {

    private Long documentationId;
    private Long dailyAttendanceId;
    private String studentName; // Consolidated from UIS
    private String studentEnrollmentNumber;

    private String submittedByUserName; // Consolidated from UIS User
    private Instant submissionDate;

    private String reasonText;
    private String documentationUrl;

    private ApprovalStatus approvalStatus;
    private String approvedByStaffName; // Consolidated from UIS Staff
    private Instant approvalDate;
    private String reviewerNotes;
}