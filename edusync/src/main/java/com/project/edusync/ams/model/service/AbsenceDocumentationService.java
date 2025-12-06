package com.project.edusync.ams.model.service;

import com.project.edusync.ams.model.dto.request.SubmitExcuseRequestDTO;
import com.project.edusync.ams.model.dto.response.AbsenceDocumentationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for absence documentation (excuse) workflow.
 */
public interface AbsenceDocumentationService {

    /**
     * Submit a new excuse/documentation for a student attendance record.
     *
     * @param request details of the submission
     * @return created documentation DTO
     * @throws com.project.edusync.ams.model.exception.AttendanceProcessingException if validation fails
     */
    AbsenceDocumentationResponseDTO submit(SubmitExcuseRequestDTO request);

    /**
     * List pending documentation entries for administrative review.
     *
     * @param pageable paging and sorting
     * @return page of DTOs
     */
    Page<AbsenceDocumentationResponseDTO> listPending(Pageable pageable);

    /**
     * Get a single documentation record by id.
     *
     * @param id documentation id
     * @return DTO for the record
     */
    AbsenceDocumentationResponseDTO getById(Long id);

    /**
     * Approve a pending documentation entry. This will also update the linked StudentDailyAttendance
     * to an 'Excused' attendance type (if configured).
     *
     * @param id documentation id
     * @param performedByStaffId staff id performing approval (for audit)
     * @return updated documentation DTO
     */
    AbsenceDocumentationResponseDTO approve(Long id, Long performedByStaffId);

    /**
     * Reject a pending documentation entry with an optional rejection reason.
     *
     * @param id documentation id
     * @param performedByStaffId staff id performing rejection
     * @param rejectionReason optional rejection reason
     * @return updated documentation DTO
     */
    AbsenceDocumentationResponseDTO reject(Long id, Long performedByStaffId, String rejectionReason);
}
