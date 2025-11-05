package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmsStudentDailyAttendanceRequest {

    // Student ID is a logical foreign key to the UIS module
    @NotNull(message = "Student ID is required.")
    private Long studentId;

    @NotNull(message = "Attendance date is required.")
    private LocalDate attendanceDate;

    // Type ID is the foreign key to AttendanceType (e.g., ID for "Present")
    @NotNull(message = "Attendance type ID is required.")
    private Long typeId;

    // The staff member ID is typically pulled from the authenticated user's token
    // but is included here for clarity if using a dedicated system user.
    @NotNull(message = "Staff ID is required.")
    private Long takenByStaffId;

    private String notes;
}