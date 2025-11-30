package com.project.edusync.ams.model.service;

import com.project.edusync.ams.model.dto.request.StaffAttendanceRequestDTO;
import com.project.edusync.ams.model.dto.response.StaffAttendanceResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffAttendanceService {

    StaffAttendanceResponseDTO createAttendance(StaffAttendanceRequestDTO request, Long performedBy);

    List<StaffAttendanceResponseDTO> bulkCreate(List<StaffAttendanceRequestDTO> requests, Long performedBy);

    Page<StaffAttendanceResponseDTO> listAttendances(Pageable pageable,
                                                     Optional<Long> staffId,
                                                     Optional<LocalDate> date);

    StaffAttendanceResponseDTO getAttendance(Long id);

    StaffAttendanceResponseDTO updateAttendance(Long id, StaffAttendanceRequestDTO request, Long performedBy);

    void deleteAttendance(Long id, Long performedBy);
}
