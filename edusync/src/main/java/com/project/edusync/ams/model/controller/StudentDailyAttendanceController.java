//package com.project.edusync.ams.model.controller;
//
//import com.project.edusync.ams.model.dto.request.AttendanceTypeRequestDTO;
//import com.project.edusync.ams.model.dto.request.StudentAttendanceRequestDTO;
//import com.project.edusync.ams.model.dto.response.AttendanceTypeResponseDTO;
//import com.project.edusync.ams.model.service.StudentDailyAttendanceService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
///**
// * REST Controller for managing all Student Daily Attendance operations.
// * NOTE: Assumes @EnableMethodSecurity is used in WebSecurityConfig to enable @PreAuthorize.
// * Assumes ${api.url} property resolves to /api/v1.
// */
//@RestController
//@RequestMapping("${api.url}/ams/records")
//@RequiredArgsConstructor
//@Slf4j
//public class StudentDailyAttendanceController {
//
//    private final StudentDailyAttendanceService studentDailyAttendanceService;
//
//    // --- Mock Authentication Helper (Replace with actual Security Context logic) ---
//    private Long getAuthenticatedStaffId(Authentication authentication) {
//        // In a real application, retrieve ID from JWT claims or custom UserDetails
//        // For testing, we use a placeholder:
//        // Example: return (Long) ((CustomUserDetails) authentication.getPrincipal()).getStaffId();
//
//        // Since the user is testing without a token, and the service requires an ID,
//        // we throw a specific error if authentication is missing, forcing proper setup.
//        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
//            log.error("Authentication required but not found in context.");
//            // Throw a custom exception that maps to 401/403
//            throw new SecurityException("Full authentication is required. Token is missing or invalid.");
//        }
//
//        try {
//            // Placeholder: Assume the principal name is the Staff ID
//            return Long.parseLong(authentication.getName());
//        } catch (NumberFormatException e) {
//            log.error("Principal name is not a valid Staff ID: {}", authentication.getName());
//            throw new SecurityException("Invalid format for authenticated Staff ID.");
//        }
//    }
//    // ----------------------------------------------------------------------------
//
//
//    /**
//     * POST /api/v1/ams/records
//     * Submits a batch of daily attendance records.
//     * Permissions: ams:record:create
//     */
//    @PostMapping
//    @PreAuthorize("hasAuthority('ams:record:create')")
//    public ResponseEntity<List<AttendanceTypeResponseDTO>> markStudentAttendanceBatch(
//            @Valid @RequestBody List<StudentAttendanceRequestDTO> requests,
//            Authentication authentication) {
//
//        log.info("Received batch POST request for {} records.", requests.size());
//
//        // Use a mock ID since the user is testing unauthenticated, but production code should look like this:
//        // Long staffId = getAuthenticatedStaffId(authentication);
//        Long staffId = 501L; // Hardcode a test staff ID for unauthenticated testing
//
//        List<AttendanceTypeResponseDTO> responses = studentDailyAttendanceService.markAttendanceBatch(requests, staffId);
//        return new ResponseEntity<>(responses, HttpStatus.CREATED);
//    }
//
//    /**
//     * GET /api/v1/ams/records
//     * Retrieves a paginated list of all student attendance records, supporting powerful filters.
//     * Permissions: ams:record:read_all
//     */
//    @GetMapping
//    @PreAuthorize("hasAuthority('ams:record:read_all')")
//    public ResponseEntity<PaginatedResponseDTO<AttendanceTypeResponseDTO>> getAttendanceRecords(
//            AttendanceTypeRequestDTO filterDTO,
//            Pageable pageable) {
//
//        log.info("Received GET request for filtered attendance. Filters: {}", filterDTO);
//        PaginatedResponseDTO<AttendanceTypeResponseDTO> response = studentDailyAttendanceService.getFilteredAttendanceRecords(filterDTO, pageable);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * GET /api/v1/ams/records/{recordId}
//     * Retrieves the details for a single, specific StudentDailyAttendance entry.
//     * Permissions: ams:record:read
//     */
//    @GetMapping("/{recordId}")
//    @PreAuthorize("hasAuthority('ams:record:read')")
//    public ResponseEntity<AttendanceTypeResponseDTO> getAttendanceRecord(@PathVariable Long recordId) {
//        log.info("Received GET request for single record ID: {}", recordId);
//        AttendanceTypeResponseDTO response = studentDailyAttendanceService.getAttendanceRecordById(recordId);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * PUT /api/v1/ams/records/{recordId}
//     * Updates a single attendance record (e.g., correcting an error).
//     * Permissions: ams:record:update
//     */
//    @PutMapping("/{recordId}")
//    @PreAuthorize("hasAuthority('ams:record:update')")
//    public ResponseEntity<AttendanceTypeResponseDTO> updateAttendanceRecord(
//            @PathVariable Long recordId,
//            @Valid @RequestBody StudentAttendanceRequestDTO request,
//            Authentication authentication) {
//
//        log.info("Received PUT request to update record ID: {}", recordId);
//        // Long staffId = getAuthenticatedStaffId(authentication);
//        Long staffId = 501L; // Hardcode a test staff ID for unauthenticated testing
//
//        AttendanceTypeResponseDTO response = studentDailyAttendanceService.updateAttendanceRecord(recordId, request, staffId);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * DELETE /api/v1/ams/records/{recordId}
//     * Deletes or voids an attendance record (soft-delete).
//     * Permissions: ams:record:delete
//     */
//    @DeleteMapping("/{recordId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PreAuthorize("hasAuthority('ams:record:delete')")
//    public void deleteAttendanceRecord(
//            @PathVariable Long recordId,
//            Authentication authentication) {
//
//        log.warn("Received DELETE request (soft-delete) for record ID: {}", recordId);
//        // Long staffId = getAuthenticatedStaffId(authentication);
//        Long staffId = 501L; // Hardcode a test staff ID for unauthenticated testing
//
//        studentDailyAttendanceService.deleteAttendanceRecord(recordId, staffId);
//    }
//}