package com.project.edusync.ams.model.controller;

import com.project.edusync.ams.model.dto.request.AttendanceTypeRequestDTO;
import com.project.edusync.ams.model.dto.response.AttendanceTypeResponseDTO;
import com.project.edusync.ams.model.exception.AttendanceTypeInUseException;
import com.project.edusync.ams.model.exception.AttendanceTypeNotFoundException;
import com.project.edusync.ams.model.service.AttendanceTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ams/types")
@RequiredArgsConstructor
public class AttendanceTypeController {

    private final AttendanceTypeService attendanceTypeService;

    /**
     * POST /api/v1/ams/types
     * Creates a new attendance type configuration.
     * Permission: ams:config:create
     */
    @PostMapping
    public ResponseEntity<AttendanceTypeResponseDTO> createType(
            @Valid @RequestBody AttendanceTypeRequestDTO requestDTO) {

        AttendanceTypeResponseDTO response = attendanceTypeService.create(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * GET /api/v1/ams/types
     * Retrieves all active attendance types for dropdowns and UI legends.
     * Permission: ams:config:read (Low-level read, usually public/authenticated)
     */
    @GetMapping
    public ResponseEntity<List<AttendanceTypeResponseDTO>> getAllTypes() {
        List<AttendanceTypeResponseDTO> response = attendanceTypeService.findAllActive();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/ams/types/{typeId}
     * Retrieves details for a specific active attendance type.
     * Permission: ams:config:read
     */
    @GetMapping("/{typeId}")
    public ResponseEntity<AttendanceTypeResponseDTO> getTypeById(@PathVariable Long typeId) {
        AttendanceTypeResponseDTO response = attendanceTypeService.findById(typeId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/ams/types/{typeId}
     * Updates an existing attendance type configuration.
     * Permission: ams:config:update
     */
    @PutMapping("/{typeId}")
    public ResponseEntity<AttendanceTypeResponseDTO> updateType(
            @PathVariable Long typeId,
            @Valid @RequestBody AttendanceTypeRequestDTO requestDTO) {

        AttendanceTypeResponseDTO response = attendanceTypeService.update(typeId, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/ams/types/{typeId}
     * Soft deletes (archives) an attendance type by setting isActive=false.
     * Returns 204 No Content on successful soft deletion.
     * Permission: ams:config:delete
     *
     * Note: We handle the specific exceptions in a centralized @ControllerAdvice
     * but define the expected behavior here.
     */
    @DeleteMapping("/{typeId}")
    public ResponseEntity<Void> deleteType(@PathVariable Long typeId) {
        attendanceTypeService.softDelete(typeId);
        return ResponseEntity.noContent().build();
    }

    // --- Centralized Exception Handling Example ---
    // In a professional project, a single @ControllerAdvice component would handle
    // all exceptions across all controllers. Here is an example of what it handles:

    @ExceptionHandler(AttendanceTypeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(AttendanceTypeNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(AttendanceTypeInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict is appropriate when business rules prevent the action
    public String handleInUseException(AttendanceTypeInUseException ex) {
        return ex.getMessage();
    }
}