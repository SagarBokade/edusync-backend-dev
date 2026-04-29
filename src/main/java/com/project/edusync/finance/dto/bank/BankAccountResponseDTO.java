package com.project.edusync.finance.dto.bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BankAccountResponseDTO(
    Long id, UUID uuid, String accountName, String accountNumber,
    String bankName, String branchName, String ifscCode, String accountType,
    Long coaAccountId, String coaAccountCode, String coaAccountName,
    BigDecimal statementBalance, BigDecimal bookBalance,
    BigDecimal difference,
    Long unreconciledCount,
    Boolean isActive, String notes,
    LocalDateTime createdAt
) {}
