package com.project.edusync.ams.model.dto.response;

import lombok.Value;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for presenting StudentDailyAttendance data to the client.
 */
@Value
public class StudentAttendanceResponseDTO {

    Long dailyAttendanceId;
    String uuid;

    /** Logical FK to UIS.Student.id */
    Long studentId;

    /** Denormalized data from UIS for display */
    String studentFullName;

    LocalDate attendanceDate;

    /** Logical FK to UIS.Staff.id */
    Long takenByStaffId;

    /** Denormalized data from UIS for display */
    String takenByStaffName;

    /** Nested DTO for the internal AttendanceType */
    AttendanceTypeResponseDTO attendanceType;

    String notes;

    /** To show if an excuse has been submitted */
    AbsenceDocumentationSummaryResponseDTO absenceDocumentation;

    LocalDateTime createdAt;
    String createdBy;
}