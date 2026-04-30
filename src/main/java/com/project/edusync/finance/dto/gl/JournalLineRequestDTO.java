package com.project.edusync.finance.dto.gl;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * A single debit or credit line within a journal entry request.
 *
 * Exactly one of debitAmount or creditAmount must be > 0.
 * Both zero or both non-zero are validation errors caught in the service layer.
 */
public record JournalLineRequestDTO(

    @NotNull(message = "Account ID is required")
    Long accountId,

    @DecimalMin(value = "0.00", message = "Debit amount cannot be negative")
    BigDecimal debitAmount,

    @DecimalMin(value = "0.00", message = "Credit amount cannot be negative")
    BigDecimal creditAmount,

    String narration
) {}
