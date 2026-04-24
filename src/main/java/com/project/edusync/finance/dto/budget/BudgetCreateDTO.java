package com.project.edusync.finance.dto.budget;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * DTO for creating a new departmental budget.
 */
public record BudgetCreateDTO(

    @NotBlank(message = "Department name is required")
    String departmentName,

    @NotBlank(message = "Academic year is required")
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Academic year must be in format YYYY-YYYY (e.g. 2025-2026)")
    String academicYear,

    String title,

    String submitterNotes,

    @NotEmpty(message = "At least one budget line item is required")
    @Valid
    List<BudgetLineItemCreateDTO> lineItems
) {}
