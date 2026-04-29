package com.project.edusync.finance.dto.statements;

import java.math.BigDecimal;
import java.util.List;

public record FinancialStatementDTO(
    String periodInfo,
    List<AccountBalanceDTO> items,
    BigDecimal totalDr,
    BigDecimal totalCr,
    BigDecimal netProfitOrLoss
) {
    public record AccountBalanceDTO(
        Long accountId,
        String accountCode,
        String accountName,
        String subType,
        BigDecimal balanceDr,
        BigDecimal balanceCr,
        BigDecimal netBalance
    ) {}
}
