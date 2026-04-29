package com.project.edusync.finance.model.enums;

/**
 * Type of transaction in a bank statement.
 */
public enum BankTransactionType {
    CREDIT,  // Money coming in (fees received, grants, etc.)
    DEBIT    // Money going out (vendor payments, salaries, etc.)
}
