package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * DTO for submitting new absence documentation (excuse).
 */
@Value
public class AbsenceDocumentationRequestDTO {

    /**
     * The ID of the StudentDailyAttendance record this documentation is for (Shared PK).
     */
    @NotNull(message = "Daily Attendance ID is required to link the excuse.")
    Long dailyAttendanceId;

    /**
     * Logical FK to UIS.User.id - the user (e.g., Parent/Staff) submitting the documentation.
     */
    @NotNull(message = "Submitted By User ID is required.")
    Long submittedByUserId;

    @NotBlank(message = "A reason text is required for absence documentation.")
    @Size(min = 10, max = 1000, message = "Reason text must be between 10 and 1000 characters.")
    String reasonText;

    /**
     * URL to a file upload (e.g., doctor's note) - optional.
     */
    @Size(max = 255, message = "Documentation URL is too long.")
    String documentationUrl;
}