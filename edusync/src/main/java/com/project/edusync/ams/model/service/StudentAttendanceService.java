package com.project.edusync.ams.model.service;

import com.project.edusync.ams.model.dto.request.StudentAttendanceRequestDTO;
import com.project.edusync.ams.model.dto.response.StudentAttendanceResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentAttendanceService {

    List<StudentAttendanceResponseDTO> markAttendanceBatch(
            List<StudentAttendanceRequestDTO> requests,
            Long performedByStaffId
    );

    Page<StudentAttendanceResponseDTO> listAttendances(
            Pageable pageable,
            Optional<Long> studentId,
            Optional<Long> takenByStaffId,
            Optional<String> fromDateIso,
            Optional<String> toDateIso,
            Optional<String> attendanceTypeShortCode
    );

    StudentAttendanceResponseDTO getAttendance(Long id);

    StudentAttendanceResponseDTO updateAttendance(
            Long recordId,
            StudentAttendanceRequestDTO req,
            Long performedByStaffId
    );

    void deleteAttendance(Long recordId, Long performedByStaffId);
}
