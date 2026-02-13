package com.project.edusync.ams.model.controller;

import com.project.edusync.ams.model.dto.request.StudentAttendanceRequestDTO;
import com.project.edusync.ams.model.dto.response.StudentAttendanceResponseDTO;
import com.project.edusync.ams.model.exception.AttendanceProcessingException;
import com.project.edusync.ams.model.service.StudentAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for Student Attendance endpoints using attendance short code.
 * Base path is configured in application properties and includes /auth/ so JWT is not required for testing
 * (WebSecurityConfig already permits /{apiVersion}/auth/**).
 *
 * NOTE: This controller intentionally **does not throw** when staff id is missing.
 * It passes the nullable performedByStaffId to the service which chooses between the header/principal
 * and the per-row takenByStaffId in the DTO (service preference: performedByStaffId -> dto.takenByStaffId).
 */
@RestController
@RequestMapping("${api.url}/auth/ams/records")
@RequiredArgsConstructor
@Slf4j
public class StudentAttendanceController {

    private final StudentAttendanceService service;

    /**
     * POST - mark a batch of student attendance.
     *
     * The controller no longer rejects when X-User-Id is missing — it forwards whatever it can
     * to the service. For testing you can either supply a header "X-User-Id" or include
     * "takenByStaffId" in each request object.
     */
    @PostMapping
    public ResponseEntity<List<StudentAttendanceResponseDTO>> createBatch(
            @RequestBody @Valid List<StudentAttendanceRequestDTO> requests,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            Authentication authentication) {

        // extract staff id if present in principal; otherwise headerUserId may be used (can be null)
        Long staffId = extractStaffId(authentication).orElse(headerUserId);

        // do NOT throw here — service will use request.takenByStaffId per-row when performedByStaffId is null
        List<StudentAttendanceResponseDTO> resp = service.markAttendanceBatch(requests, staffId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    /**
     * GET list with optional filters.
     */
    @GetMapping
    public ResponseEntity<Page<StudentAttendanceResponseDTO>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id,desc") String sort,
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestParam(value = "takenByStaffId", required = false) Long takenByStaffId,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate,
            @RequestParam(value = "attendanceTypeShortCode", required = false) String attendanceTypeShortCode
    ) {
        String[] sortParts = sort.split(",");
        Sort s;
        if (sortParts.length >= 2) {
            s = Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0]);
        } else {
            s = Sort.by(Sort.Direction.DESC, sortParts[0]);
        }
        Pageable pageable = PageRequest.of(page, size, s);
        Page<StudentAttendanceResponseDTO> resp = service.listAttendances(pageable,
                Optional.ofNullable(studentId),
                Optional.ofNullable(takenByStaffId),
                Optional.ofNullable(fromDate),
                Optional.ofNullable(toDate),
                Optional.ofNullable(attendanceTypeShortCode));
        return ResponseEntity.ok(resp);
    }

    /**
     * GET single record
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentAttendanceResponseDTO> getById(@PathVariable Long id) {
        StudentAttendanceResponseDTO resp = service.getAttendance(id);
        return ResponseEntity.ok(resp);
    }

    /**
     * PUT update
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudentAttendanceResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid StudentAttendanceRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            Authentication authentication) {

        Long staffId = extractStaffId(authentication).orElse(headerUserId);
        // do NOT throw here; pass nullable staffId to service
        StudentAttendanceResponseDTO resp = service.updateAttendance(id, request, staffId);
        return ResponseEntity.ok(resp);
    }

    /**
     * DELETE soft-delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            Authentication authentication) {

        Long staffId = extractStaffId(authentication).orElse(headerUserId);
        service.deleteAttendance(id, staffId);
    }

    // Helper: extract staff ID from Authentication principal (if your principal stores it)
    private Optional<Long> extractStaffId(Authentication authentication) {
        if (authentication == null) return Optional.empty();
        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            try {
                return Optional.of(Long.parseLong(user.getUsername()));
            } catch (NumberFormatException ignored) {}
        }
        // Add additional principal types if you use a custom JWT principal that stores the ID in claims
        return Optional.empty();
    }
}
