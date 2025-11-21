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
 * DTO for creating or updating a StaffDailyAttendance record (e.g., clocking in/out).
 */
@Value
public class StaffAttendanceRequestDTO {

    /** Logical Foreign Key to UIS.Staff.id. */
    @NotNull(message = "Staff ID is required")
    Long staffId;

    @NotNull(message = "Attendance date is required")
    @PastOrPresent(message = "Attendance date cannot be in the future")
    LocalDate attendanceDate;

    /** JPA Foreign Key to AMS.AttendanceType.id. */
    @NotNull(message = "Attendance Type ID is required")
    Long attendanceTypeId;

    LocalTime timeIn;
    LocalTime timeOut;

    /** Total hours worked, used for manual entry or system calculated override. */
    @DecimalMin(value = "0.0", message = "Total hours must be non-negative")
    Double totalHours;

    @NotNull(message = "Attendance source must be specified.")
    AttendanceSource source; // e.g., MANUAL, BIOMETRIC, SYSTEM

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    String notes;
}