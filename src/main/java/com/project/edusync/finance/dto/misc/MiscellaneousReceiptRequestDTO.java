package com.project.edusync.finance.dto.misc;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MiscellaneousReceiptRequestDTO(
    @NotNull(message = "Receipt date is required.") LocalDate receiptDate,
    @NotBlank(message = "Received from is required.") String receivedFrom,
    @NotBlank(message = "Description is required.") String description,
    @NotNull(message = "Amount is required.") @Positive(message = "Amount must be greater than zero.") BigDecimal amount,
    @NotBlank(message = "Payment mode is required.") String paymentMode,
    String referenceNumber, // Cheque/UTR
    @NotNull(message = "Income account ID is required.") Long incomeAccountId,
    @NotNull(message = "Deposit account ID is required.") Long depositAccountId
) {}
