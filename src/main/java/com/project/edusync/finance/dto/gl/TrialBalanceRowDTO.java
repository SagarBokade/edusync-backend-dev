package com.project.edusync.finance.dto.gl;

import com.project.edusync.finance.model.enums.AccountType;

import java.math.BigDecimal;

/**
 * A single row in the Trial Balance report.
 *
 * Trial Balance rule: SUM of all debitBalance == SUM of all creditBalance
 * (across all accounts at a point in time).
 */
public record TrialBalanceRowDTO(
    Long accountId,
    String accountCode,
    String accountName,
    AccountType accountType,
    /** Total debits posted to this account (all time, posted entries only). */
    BigDecimal totalDebits,
    /** Total credits posted to this account. */
    BigDecimal totalCredits,
    /**
     * Net balance:
     *   For ASSET/EXPENSE accounts: netBalance = totalDebits - totalCredits (positive = debit balance)
     *   For LIABILITY/EQUITY/INCOME: netBalance = totalCredits - totalDebits (positive = credit balance)
     */
    BigDecimal netBalance,
    /** Whether net balance is a debit (true) or credit (false). */
    boolean isDebitBalance
) {}
