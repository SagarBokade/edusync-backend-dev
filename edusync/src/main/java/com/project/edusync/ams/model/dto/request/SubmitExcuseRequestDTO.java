package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;

@Value
public class SubmitExcuseRequestDTO {

    @NotNull(message = "attendanceId is required")
    Long attendanceId;

    @NotNull(message = "submittedByParentId is required")
    Long submittedByParentId;

    @Size(max = 1000, message = "Document URL cannot exceed 1000 chars")
    String documentUrl; // optional pointer to uploaded file

    @Size(max = 1000, message = "Note cannot exceed 1000 chars")
    String note;

    /**
     * Use attendanceDate for verification if you want (optional).
     * If provided, must be PastOrPresent - not future dates.
     */
    @PastOrPresent(message = "Attendance date cannot be in future")
    LocalDate attendanceDate;
}
