package com.project.edusync.finance.dto.bank;

import com.project.edusync.finance.model.enums.BankTransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BankTransactionRequestDTO(
    @NotNull Long bankAccountId,
    @NotNull LocalDate transactionDate,
    LocalDate valueDate,
    @NotBlank String description,
    String referenceNumber,
    String instrumentNumber,
    @NotNull BankTransactionType transactionType,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    BigDecimal runningBalance
) {}
