package com.project.edusync.ams.model.dto.request;

import com.project.edusync.ams.model.enums.AttendanceSource;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for creating or updating a StaffDailyAttendance record.
 * Uses attendanceShortCode (e.g., "P","A","L") for consistency with Student APIs.
 */
@Value
public class StaffAttendanceRequestDTO {

    /** Logical FK to UIS.Staff.id. */
    @NotNull(message = "Staff ID is required")
    Long staffId;

    @NotNull(message = "Attendance date is required")
    @PastOrPresent(message = "Attendance date cannot be in the future")
    LocalDate attendanceDate;

    /** Short code representing attendance type (P/A/L/etc.) - preferred over numeric type id */
    @NotNull(message = "Attendance short code is required")
    String attendanceShortCode;

    LocalTime timeIn;
    LocalTime timeOut;

    /** Total hours worked (manual override). */
    @DecimalMin(value = "0.0", message = "Total hours must be non-negative")
    Double totalHours;

    /** Strong-typed source */
    @NotNull(message = "Attendance source must be specified.")
    AttendanceSource source; // e.g., MANUAL, BIOMETRIC, SYSTEM

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    String notes;
}
