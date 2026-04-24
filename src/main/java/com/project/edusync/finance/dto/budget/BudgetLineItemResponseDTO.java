package com.project.edusync.finance.dto.budget;

import java.math.BigDecimal;

/**
 * Response DTO for a single budget line item, including variance data.
 */
public record BudgetLineItemResponseDTO(
    Long lineItemId,
    String category,
    Long linkedAccountId,
    String linkedAccountCode,
    String linkedAccountName,
    BigDecimal allocatedAmount,
    BigDecimal actualAmount,
    BigDecimal variance,
    /** Utilisation percentage for this line: (actual / allocated) × 100 */
    double utilisationPct,
    /** Traffic-light status derived from utilisation: UNDER / ON_TRACK / OVER */
    String varianceStatus,
    String notes,
    Integer lineNumber
) {}
