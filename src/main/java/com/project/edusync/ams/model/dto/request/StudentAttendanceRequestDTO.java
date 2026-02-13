package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.time.LocalDate;

/**
 * DTO for creating a single StudentDailyAttendance record.
 */
@Value
public class StudentAttendanceRequestDTO {

    /**
     * Logical Foreign Key to UIS.Student.id. Cannot be null.
     */
    @NotNull(message = "Student ID is required")
    Long studentId;

    /**
     * JPA Foreign Key to AMS.AttendanceType.id. Cannot be null.
     */
    @NotBlank(message = "Attendance short code (P, A, L) is required.")
    @Size(min = 1, max = 10, message = "Short code must be between 1 and 10 characters.")
    String attendanceShortCode; // Use P, A, or L

    @NotNull(message = "Attendance date is required")
    @PastOrPresent(message = "Attendance date cannot be in the future")
    LocalDate attendanceDate;

    /**
     * Logical Foreign Key to UIS.Staff.id (the staff member taking attendance).
     */
    @NotNull(message = "Staff ID is required for accountability")
    Long takenByStaffId;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    String notes;
}