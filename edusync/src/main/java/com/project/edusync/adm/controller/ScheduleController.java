package com.project.edusync.adm.controller;

import com.project.edusync.adm.model.dto.request.ScheduleRequestDto;
import com.project.edusync.adm.model.dto.response.ScheduleResponseDto;
import com.project.edusync.adm.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing the Timetable Schedule.
 */
@RestController
@RequestMapping("${api.url}/auth") // Following your existing request mapping
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * Fetches all existing Schedule entries for a specific section.
     * HTTP 200 OK on success.
     */
    @GetMapping("/sections/{sectionId}/schedule")
    public ResponseEntity<List<ScheduleResponseDto>> getScheduleForSection(
            @PathVariable UUID sectionId) {

        List<ScheduleResponseDto> response = scheduleService.getScheduleForSection(sectionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Creates a new Schedule entry.
     * HTTP 201 Created on success.
     * HTTP 409 Conflict if there is a double booking.
     */
    @PostMapping("/schedules")
    public ResponseEntity<ScheduleResponseDto> createSchedule(
            @Valid @RequestBody ScheduleRequestDto requestDto) {

        ScheduleResponseDto createdSchedule = scheduleService.addSchedule(requestDto);
        return new ResponseEntity<>(createdSchedule, HttpStatus.CREATED);
    }

    /**
     * Updates an existing Schedule entry by its UUID.
     * HTTP 200 OK on success.
     * HTTP 409 Conflict if there is a double booking.
     */
    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateScheduleById(
            @PathVariable UUID scheduleId,
            @Valid @RequestBody ScheduleRequestDto scheduleRequestDto) {

        ScheduleResponseDto response = scheduleService.updateSchedule(scheduleId, scheduleRequestDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Soft deletes a Schedule entry by its UUID.
     * HTTP 204 No Content on success.
     */
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteScheduleById(@PathVariable UUID scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}