package com.project.edusync.ams.model.dto.response;

import com.project.edusync.ams.model.enums.ApprovalStatus;
import lombok.Value;

/**
 * Summary DTO used to embed the absence excuse status within a StudentAttendanceResponseDTO.
 */
@Value
public class AbsenceDocumentationSummaryResponseDTO {

    Long dailyAttendanceId; // Same as documentationId
    ApprovalStatus approvalStatus;
    String documentationUrl; // Quick link to the document, if available
}