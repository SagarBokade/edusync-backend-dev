package com.project.edusync.finance.dto.misc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MiscellaneousReceiptResponseDTO(
    Long id,
    String receiptNumber,
    LocalDate receiptDate,
    String receivedFrom,
    String description,
    BigDecimal amount,
    String paymentMode,
    String referenceNumber,
    Long incomeAccountId,
    String incomeAccountCode,
    String incomeAccountName,
    Long depositAccountId,
    String depositAccountCode,
    String depositAccountName,
    Long glEntryId,
    LocalDateTime createdAt,
    String createdBy
) {}
