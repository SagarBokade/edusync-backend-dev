package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmsAbsenceSubmissionRequest {

    // The ID of the specific daily attendance record being excused.
    @NotNull(message = "Daily attendance ID is required.")
    private Long dailyAttendanceId;

    // The user ID of the parent submitting the request (pulled from token).
    @NotNull(message = "Submitter user ID is required.")
    private Long submittedByUserId;

    @NotBlank(message = "A reason is required to submit documentation.")
    private String reasonText;

    private String documentationUrl; // Secure link to uploaded document
}