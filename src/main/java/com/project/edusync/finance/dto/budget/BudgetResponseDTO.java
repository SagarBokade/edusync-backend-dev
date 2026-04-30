package com.project.edusync.finance.dto.budget;

import com.project.edusync.finance.model.enums.BudgetStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Full budget response including all line items and computed variance data.
 */
public record BudgetResponseDTO(
    Long id,
    UUID uuid,
    String departmentName,
    String academicYear,
    String title,
    BigDecimal totalAllocated,
    BigDecimal totalSpent,
    /** totalAllocated - totalSpent. Positive = under budget. Negative = over budget. */
    BigDecimal totalVariance,
    /** Utilisation percentage: (totalSpent / totalAllocated) × 100 */
    double utilisationPct,
    BudgetStatus status,
    String approvedBy,
    String reviewerNotes,
    String submitterNotes,
    List<BudgetLineItemResponseDTO> lineItems,
    LocalDateTime createdAt,
    String createdBy,
    LocalDateTime updatedAt
) {}
