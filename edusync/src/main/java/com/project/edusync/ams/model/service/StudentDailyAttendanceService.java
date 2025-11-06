package com.project.edusync.ams.model.service;

import com.project.edusync.ams.model.dto.request.StudentAttendanceRequestDTO;
import com.project.edusync.ams.model.dto.response.AttendanceTypeResponseDTO;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Service interface for managing Student Daily Attendance Records.
 * Defines the contract for all CRUD and business logic operations.
 */
public interface StudentDailyAttendanceService {

    /**
     * POST: Submits a batch of daily attendance records for a class roster.
     * Enforces business rules like required notes for absence/late marks.
     *
     * @param requests The list of attendance DTOs.
     * @param authenticatedStaffId The ID of the staff member from the security context.
     * @return A list of responses for the successfully created records.
     */
    @Transactional
    List<AttendanceTypeResponseDTO> markAttendanceBatch(List<StudentAttendanceRequestDTO> requests, Long authenticatedStaffId);

    /**
     * GET: Retrieves a paginated and filtered list of all student attendance records.
     *
     * @param filterDTO The DTO containing filters (studentId, staffId, date range).
     * @param pageable The pagination and sorting information.
     * @return A paginated list of attendance record responses.
     */
//    PaginatedResponseDTO<AmsDailyAttendanceResponse> getFilteredAttendanceRecords(AttendanceRecordFilterDTO filterDTO, Pageable pageable);

    /**
     * GET: Retrieves the details for a single, specific StudentDailyAttendance entry.
     *
     * @param recordId The ID of the record to retrieve.
     * @return The details of the attendance record.
     */
    AttendanceTypeResponseDTO getAttendanceRecordById(Long recordId);

    /**
     * PUT: Updates a single attendance record.
     *
     * @param recordId The ID of the record to update.
     * @param request The DTO containing the new data.
     * @param authenticatedStaffId The ID of the staff member from the security context (for audit).
     * @return The updated attendance record response.
     */
    @Transactional
    AttendanceTypeResponseDTO updateAttendanceRecord(Long recordId, StudentAttendanceRequestDTO request, Long authenticatedStaffId);

    /**
     * DELETE: Deletes (soft-deletes) an attendance record.
     *
     * @param recordId The ID of the record to delete.
     * @param authenticatedStaffId The ID of the staff member from the security context (for audit).
     */
//    @Transactional
//    void deleteAttendanceRecord(Long recordId, Long authenticatedStaffId);
}