package com.project.edusync.em.model.dto.response;

import com.project.edusync.em.model.enums.ExamAttendanceStatus;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExamRoomStudentResponseDTO {
    Long studentId;
    Integer rollNo;
    String name;
    String className;
    String seatPosition;
    String seatLabel;
    ExamAttendanceStatus status;
    boolean finalized;
}

