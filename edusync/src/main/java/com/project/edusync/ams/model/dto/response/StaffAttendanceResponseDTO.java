package com.project.edusync.ams.model.dto.response;

import com.project.edusync.ams.model.enums.AttendanceSource;
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
public class StaffAttendanceResponseDTO {

    private Long staffAttendanceId;
    private Long staffId;

    // Data consolidated from UIS/Staff module
    private String staffName;
    private String jobTitle;

    private LocalDate attendanceDate;

    // Attendance Mark Details (from AttendanceType)
    private String attendanceMark;
    private String shortCode;
    private String colorCode;

    // Time tracking details
    private LocalTime timeIn;
    private LocalTime timeOut;
    private Double totalHours;

    private AttendanceSource source;
    private String notes;
}