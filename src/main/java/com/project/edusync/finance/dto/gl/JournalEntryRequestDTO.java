package com.project.edusync.finance.dto.gl;

import com.project.edusync.finance.model.enums.JournalReferenceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating a manual Journal Entry from the GL screen.
 * Auto-entries (from payments, payroll, etc.) bypass this DTO and use the service directly.
 */
public record JournalEntryRequestDTO(

    @NotNull(message = "Entry date is required")
    LocalDate entryDate,

    @NotBlank(message = "Description is required")
    String description,

    JournalReferenceType referenceType,  // defaults to MANUAL if not provided

    Long referenceId,

    @NotEmpty(message = "At least two journal lines are required")
    @Valid
    List<JournalLineRequestDTO> lines
) {}
