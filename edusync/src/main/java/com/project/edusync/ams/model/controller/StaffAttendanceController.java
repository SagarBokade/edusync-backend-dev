package com.project.edusync.ams.model.controller;

import com.project.edusync.ams.model.dto.request.StaffAttendanceRequestDTO;
import com.project.edusync.ams.model.dto.response.StaffAttendanceResponseDTO;
import com.project.edusync.ams.model.service.StaffAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "${api.url:/api/v1}/auth/ams/staff", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class StaffAttendanceController {

    private final StaffAttendanceService service;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffAttendanceResponseDTO> create(
            @Valid @RequestBody StaffAttendanceRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {

        log.debug("POST staff attendance request: staffId={}, date={}", request.getStaffId(), request.getAttendanceDate());
        StaffAttendanceResponseDTO dto = service.createAttendance(request, headerUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping(path = "/bulk", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<StaffAttendanceResponseDTO>> bulkCreate(
            @Valid @RequestBody List<StaffAttendanceRequestDTO> requests,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {

        log.debug("POST bulk staff attendance request, count={}", requests == null ? 0 : requests.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.bulkCreate(requests, headerUserId));
    }

    @GetMapping
    public ResponseEntity<Page<StaffAttendanceResponseDTO>> list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "id,desc") String sort,
            @RequestParam(value = "staffId", required = false) Long staffId,
            @RequestParam(value = "date", required = false) String dateStr) {

        String[] sortParts = sort.split(",");
        Sort s = sortParts.length >= 2
                ? Sort.by(Sort.Direction.fromString(sortParts[1]), sortParts[0])
                : Sort.by(Sort.Direction.DESC, sortParts[0]);

        Pageable pageable = PageRequest.of(page, size, s);
        Optional<LocalDate> date = Optional.ofNullable(dateStr).filter(s1 -> !s1.isBlank()).map(LocalDate::parse);

        return ResponseEntity.ok(service.listAttendances(pageable, Optional.ofNullable(staffId), date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffAttendanceResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAttendance(id));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffAttendanceResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody StaffAttendanceRequestDTO request,
            @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {

        return ResponseEntity.ok(service.updateAttendance(id, request, headerUserId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @RequestHeader(value = "X-User-Id", required = false) Long headerUserId) {
        service.deleteAttendance(id, headerUserId);
    }
}
