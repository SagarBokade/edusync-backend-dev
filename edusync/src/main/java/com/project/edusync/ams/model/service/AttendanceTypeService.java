package com.project.edusync.ams.model.service;

import com.project.edusync.ams.model.dto.request.AttendanceTypeRequestDTO;
import com.project.edusync.ams.model.dto.response.AttendanceTypeResponseDTO;

import java.util.List;

/**
 * Interface defining the business logic contract for managing Attendance Type configurations.
 */
public interface AttendanceTypeService {

    /**
     * Creates a new Attendance Type configuration.
     * @param requestDTO The DTO containing the new type details.
     * @return The created AttendanceType as a Response DTO.
     */
    AttendanceTypeResponseDTO create(AttendanceTypeRequestDTO requestDTO);

    /**
     * Retrieves all active Attendance Types.
     * @return A list of all active types.
     */
    List<AttendanceTypeResponseDTO> findAllActive();

    /**
     * Retrieves a single active Attendance Type by ID.
     * @param typeId The ID of the type to retrieve.
     * @return The AttendanceType as a Response DTO.
     */
    AttendanceTypeResponseDTO findById(Long typeId);

    /**
     * Updates an existing active Attendance Type.
     * @param typeId The ID of the type to update.
     * @param requestDTO The DTO containing the updated details.
     * @return The updated AttendanceType as a Response DTO.
     */
    AttendanceTypeResponseDTO update(Long typeId, AttendanceTypeRequestDTO requestDTO);

    /**
     * Implements soft deletion (archiving) by setting the 'isActive' flag to false.
     * Performs a crucial check to ensure the type is not actively in use.
     * @param typeId The ID of the type to archive.
     */
    void softDelete(Long typeId);
}