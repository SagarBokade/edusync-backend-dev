package com.project.edusync.finance.dto.budget;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for the Finance Admin to approve or reject a budget.
 */
public record BudgetApprovalDTO(

    @NotNull
    boolean approved,

    /** Explanation for rejection or revision request (required when approved = false). */
    String reviewerNotes
) {}
