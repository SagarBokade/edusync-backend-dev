package com.project.edusync.ams.model.dto.request;

import com.project.edusync.ams.model.enums.AttendanceSource;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmsStaffDailyAttendanceRequest {

    @NotNull(message = "Staff ID is required.")
    private Long staffId;

    @NotNull(message = "Attendance date is required.")
    private LocalDate attendanceDate;

    @NotNull(message = "Attendance type ID is required.")
    private Long typeId;

    private LocalTime timeIn;

    private LocalTime timeOut;

    private AttendanceSource source = AttendanceSource.MANUAL;

    private String notes;
}