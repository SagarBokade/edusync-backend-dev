package com.project.edusync.finance.dto.bank;

import com.project.edusync.finance.model.enums.BankTransactionType;
import com.project.edusync.finance.model.enums.ReconciliationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BankTransactionResponseDTO(
    Long id, Long bankAccountId, String bankAccountName,
    LocalDate transactionDate, LocalDate valueDate,
    String description, String referenceNumber, String instrumentNumber,
    BankTransactionType transactionType, BigDecimal amount, BigDecimal runningBalance,
    ReconciliationStatus reconciliationStatus,
    Long matchedGlEntryId, String reconciliationNotes,
    LocalDateTime createdAt
) {}
