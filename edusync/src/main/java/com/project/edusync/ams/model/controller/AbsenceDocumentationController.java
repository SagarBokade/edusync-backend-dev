package com.project.edusync.ams.model.controller;

import com.project.edusync.ams.model.dto.request.SubmitExcuseRequestDTO;
import com.project.edusync.ams.model.dto.response.AbsenceDocumentationResponseDTO;
import com.project.edusync.ams.model.service.AbsenceDocumentationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for Absence / Excuse workflow.
 * Base path uses testing-friendly ${api.url:/api/v1}/auth/ams/excuses
 */
@RestController
@RequestMapping("${api.url:/api/v1}/auth/ams/excuses")
@RequiredArgsConstructor
@Slf4j
public class AbsenceDocumentationController {

    private final AbsenceDocumentationService service;

    /**
     * Submit an excuse documentation for an attendance record.
     * POST /auth/ams/excuses/submit
     */
    @PostMapping(path = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbsenceDocumentationResponseDTO> submit(@Valid @RequestBody SubmitExcuseRequestDTO req) {
        log.debug("Submit excuse for attendanceId={}, by user={}", req.getAttendanceId(), req.getSubmittedByParentId());
        AbsenceDocumentationResponseDTO dto = service.submit(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * List pending documentation entries (paginated).
     * GET /auth/ams/excuses/pending
     */
    @GetMapping(path = "/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AbsenceDocumentationResponseDTO>> pending(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.listPending(pageable));
    }

    /**
     * Get documentation by id.
     * GET /auth/ams/excuses/{docId}
     */
    @GetMapping(path = "/{docId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbsenceDocumentationResponseDTO> get(@PathVariable Long docId) {
        return ResponseEntity.ok(service.getById(docId));
    }

    /**
     * Approve a pending documentation (marks StudentDailyAttendance to Excused).
     * POST /auth/ams/excuses/{docId}/approve
     */
    @PostMapping(path = "/{docId}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbsenceDocumentationResponseDTO> approve(
            @PathVariable Long docId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {

        AbsenceDocumentationResponseDTO dto = service.approve(docId, headerUserId);
        return ResponseEntity.ok(dto);
    }

    /**
     * Reject a pending documentation with optional reason in the body.
     * POST /auth/ams/excuses/{docId}/reject
     */
    @PostMapping(path = "/{docId}/reject", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AbsenceDocumentationResponseDTO> reject(
            @PathVariable Long docId,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @RequestBody(required = false) Map<String, String> body) {

        String reason = body == null ? null : body.get("rejectionReason");
        AbsenceDocumentationResponseDTO dto = service.reject(docId, headerUserId, reason);
        return ResponseEntity.ok(dto);
    }
}
