package com.project.edusync.ams.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmsDailyAttendanceResponse {

    private Long dailyAttendanceId;
    private Long studentId;
    private String studentName; // Consolidated from UIS UserProfile
    private LocalDate attendanceDate;

    // Attendance Type Details (Pulled from joined AttendanceType)
    private String attendanceMark;
    private String shortCode;
    private boolean isAbsenceMark;
    private String colorCode;

    // Workflow Status
    private boolean hasDocumentation;
    private String documentationStatus; // e.g., "PENDING" or "APPROVED"

    private String notes;
}