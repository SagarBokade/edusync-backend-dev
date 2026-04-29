package com.project.edusync.finance.dto.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * A single line item within a budget request.
 */
public record BudgetLineItemCreateDTO(

    @NotBlank(message = "Category is required")
    String category,

    /** Optional COA account ID — enables auto-tracking of actual spending. */
    Long linkedAccountId,

    @NotNull(message = "Allocated amount is required")
    @DecimalMin(value = "0.01", message = "Allocated amount must be positive")
    BigDecimal allocatedAmount,

    String notes
) {}
