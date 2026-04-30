package com.project.edusync.finance.dto.budget;

import com.project.edusync.finance.model.enums.BudgetStatus;

import java.math.BigDecimal;

/**
 * Lightweight summary DTO for the budget list view.
 * Does NOT include line items to keep the list efficient.
 */
public record BudgetSummaryDTO(
    Long id,
    String departmentName,
    String academicYear,
    String title,
    BigDecimal totalAllocated,
    BigDecimal totalSpent,
    BigDecimal totalVariance,
    double utilisationPct,
    BudgetStatus status,
    String createdBy
) {}
