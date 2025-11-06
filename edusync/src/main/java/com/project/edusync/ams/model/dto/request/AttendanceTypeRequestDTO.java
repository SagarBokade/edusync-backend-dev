package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

/**
 * DTO used for client requests (POST, PUT) to create or update an AttendanceType.
 */
@Value
public class AttendanceTypeRequestDTO {

    @NotBlank(message = "Type name cannot be empty")
    @Size(max = 50, message = "Type name must be less than 50 characters")
    String typeName; // e.g., "Present", "Unexcused Absence"

    @NotBlank(message = "Short code cannot be empty")
    @Size(min = 1, max = 10, message = "Short code must be between 1 and 10 characters")
    String shortCode; // e.g., "P", "A", "UA"

    @NotNull(message = "Presence mark must be defined.")
    Boolean isPresentMark;

    @NotNull(message = "Absence mark must be defined.")
    Boolean isAbsenceMark;

    @NotNull(message = "Late mark must be defined.")
    Boolean isLateMark;

    @Size(max = 7, message = "Color code must be a valid hex code (e.g., #FF0000)")
    String colorCode; // Hex color for UI display
}