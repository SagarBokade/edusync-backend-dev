package com.project.edusync.ams.model.dto.response;

import com.project.edusync.ams.model.enums.AttendanceSource;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffAttendanceResponseDTO {

    private Long staffAttendanceId;
    private Long staffId;
    private String staffName;
    private String jobTitle;

    private LocalDate attendanceDate;

    private String attendanceMark;
    private String shortCode;
    private String colorCode;

    private LocalTime timeIn;
    private LocalTime timeOut;
    private Double totalHours;

    private AttendanceSource source;
    private String notes;
}
