package com.project.edusync.finance.dto.gl;

import java.math.BigDecimal;

/**
 * Response DTO for a single journal line.
 */
public record JournalLineResponseDTO(
    Long lineId,
    Long accountId,
    String accountCode,
    String accountName,
    BigDecimal debitAmount,
    BigDecimal creditAmount,
    String narration,
    Integer lineNumber
) {}
