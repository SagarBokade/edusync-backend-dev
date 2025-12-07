package com.project.edusync.ams.model.service.implementation;

import com.project.edusync.ams.model.dto.request.SubmitExcuseRequestDTO;
import com.project.edusync.ams.model.dto.response.AbsenceDocumentationResponseDTO;
import com.project.edusync.ams.model.entity.AbsenceDocumentation;
import com.project.edusync.ams.model.entity.AttendanceType;
import com.project.edusync.ams.model.entity.StudentDailyAttendance;
import com.project.edusync.ams.model.enums.ApprovalStatus;
import com.project.edusync.ams.model.exception.AttendanceProcessingException;
import com.project.edusync.ams.model.exception.AttendanceRecordNotFoundException;
import com.project.edusync.ams.model.repository.AbsenceDocumentationRepository;
import com.project.edusync.ams.model.repository.AttendanceTypeRepository;
import com.project.edusync.ams.model.repository.StudentDailyAttendanceRepository;
import com.project.edusync.ams.model.service.AbsenceDocumentationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Production-ready implementation of AbsenceDocumentationService.
 * - Prevents duplicate submissions by same user for same attendance.
 * - Ensures only PENDING docs can be approved/rejected.
 * - Sets StudentDailyAttendance to 'Excused' when approval happens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AbsenceDocumentationServiceImpl implements AbsenceDocumentationService {

    private final AbsenceDocumentationRepository repo;
    private final StudentDailyAttendanceRepository attendanceRepo;
    private final AttendanceTypeRepository attendanceTypeRepo;

    @Override
    @Transactional
    public AbsenceDocumentationResponseDTO submit(SubmitExcuseRequestDTO request) {
        // Verify attendance exists
        StudentDailyAttendance attendance = attendanceRepo.findById(request.getAttendanceId())
                .orElseThrow(() -> new AttendanceRecordNotFoundException("Attendance not found: " + request.getAttendanceId()));

        // Optional: verify attendanceDate if provided to avoid mismatches
        if (request.getAttendanceDate() != null && !attendance.getAttendanceDate().equals(request.getAttendanceDate())) {
            throw new AttendanceProcessingException("Attendance date mismatch for attendance id: " + request.getAttendanceId());
        }

        // Prevent duplicate submission by the same user for the same attendance
        boolean already = repo.existsByAttendanceIdAndSubmittedByUserId(request.getAttendanceId(), request.getSubmittedByParentId());
        if (already) {
            // You may change behavior to return existing record instead of throwing
            throw new AttendanceProcessingException("An excuse has already been submitted by this user for this attendance.");
        }

        AbsenceDocumentation doc = new AbsenceDocumentation();
        doc.setAttendance(attendance);
        doc.setSubmittedByUserId(request.getSubmittedByParentId());
        doc.setDocumentationUrl(request.getDocumentUrl());
        doc.setReasonText(request.getNote());
        doc.setApprovalStatus(ApprovalStatus.PENDING);

        AbsenceDocumentation saved = repo.save(doc);
        log.info("Submitted absence documentation id={} for attendanceId={}", saved.getId(), attendance.getId());
        return toDto(saved);
    }

    @Override
    public Page<AbsenceDocumentationResponseDTO> listPending(Pageable pageable) {
        Page<AbsenceDocumentation> page = repo.findByApprovalStatus(ApprovalStatus.PENDING, pageable);
        return page.map(this::toDto);
    }

    @Override
    public AbsenceDocumentationResponseDTO getById(Long id) {
        AbsenceDocumentation doc = repo.findById(id)
                .orElseThrow(() -> new AttendanceRecordNotFoundException("Absence documentation not found: " + id));
        return toDto(doc);
    }

    @Override
    @Transactional
    public AbsenceDocumentationResponseDTO approve(Long id, Long performedByStaffId) {
        AbsenceDocumentation doc = repo.findById(id)
                .orElseThrow(() -> new AttendanceRecordNotFoundException("Absence documentation not found: " + id));

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new AttendanceProcessingException("Only pending documents can be approved");
        }

        // Find 'Excused' attendance type (fallbacks)
        AttendanceType excusedType = findExcusedAttendanceType()
                .orElseThrow(() -> new AttendanceProcessingException("No 'Excused' attendance type configured"));

        // Update attendance
        StudentDailyAttendance attendance = doc.getAttendance();
        attendance.setAttendanceType(excusedType);
        attendanceRepo.save(attendance);

        // Update documentation
        doc.setApprovalStatus(ApprovalStatus.APPROVED);
        doc.setApprovedByStaffId(performedByStaffId);
        doc.setDecisionAt(LocalDateTime.now());
        doc.setReviewerNotes("Approved by staff id: " + performedByStaffId);
        AbsenceDocumentation updated = repo.save(doc);

        log.info("Approved documentation id={} for attendanceId={} by staffId={}", updated.getId(), attendance.getId(), performedByStaffId);
        return toDto(updated);
    }

    @Override
    @Transactional
    public AbsenceDocumentationResponseDTO reject(Long id, Long performedByStaffId, String rejectionReason) {
        AbsenceDocumentation doc = repo.findById(id)
                .orElseThrow(() -> new AttendanceRecordNotFoundException("Absence documentation not found: " + id));

        if (doc.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new AttendanceProcessingException("Only pending documents can be rejected");
        }

        doc.setApprovalStatus(ApprovalStatus.REJECTED);
        doc.setReviewerNotes(rejectionReason);
        doc.setApprovedByStaffId(performedByStaffId);
        doc.setDecisionAt(LocalDateTime.now());
        AbsenceDocumentation updated = repo.save(doc);

        log.info("Rejected documentation id={} for attendanceId={} by staffId={}, reason={}", updated.getId(), doc.getAttendance().getId(), performedByStaffId, rejectionReason);
        return toDto(updated);
    }

    /* ------------------ helpers ------------------ */

    /**
     * Find an attendance type that represents "Excused".
     * Checks common short codes (E, EX) then searches for "exc" in type name.
     */
    private Optional<AttendanceType> findExcusedAttendanceType() {
        Optional<AttendanceType> opt = attendanceTypeRepo.findByShortCodeIgnoreCase("E");
        if (opt.isPresent()) return opt;

        opt = attendanceTypeRepo.findByShortCodeIgnoreCase("EX");
        if (opt.isPresent()) return opt;

        return attendanceTypeRepo.findByTypeNameContainingIgnoreCase("exc");
    }

    /**
     * Convert entity -> your existing DTO structure.
     * Order of arguments must match the constructor of AbsenceDocumentationResponseDTO (@Value).
     */
    private AbsenceDocumentationResponseDTO toDto(AbsenceDocumentation d) {
        Long id = d.getId();
        String uuid = d.getUuid() == null ? null : d.getUuid().toString();
        String reason = d.getReasonText();
        String url = d.getDocumentationUrl();
        ApprovalStatus status = d.getApprovalStatus();
        String reviewerNotes = d.getReviewerNotes();
        Long submittedBy = d.getSubmittedByUserId();
        String submittedByName = null; // denormalized - you can fetch name from UIS if required
        Long approvedByStaffId = d.getApprovedByStaffId();
        String approvedByStaffName = null; // denormalized - optional
        java.time.LocalDateTime createdAt = d.getCreatedAt();

        return new AbsenceDocumentationResponseDTO(
                id,
                uuid,
                reason,
                url,
                status,
                reviewerNotes,
                submittedBy,
                submittedByName,
                approvedByStaffId,
                approvedByStaffName,
                createdAt
        );
    }
}
