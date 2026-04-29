package com.project.edusync.finance.model.enums;

/**
 * Represents the five fundamental account types in double-entry accounting.
 * Uses the accounting equation: Assets = Liabilities + Equity
 *
 * Normal balance rules:
 *   ASSET    → Debit increases, Credit decreases
 *   EXPENSE  → Debit increases, Credit decreases
 *   LIABILITY → Credit increases, Debit decreases
 *   EQUITY   → Credit increases, Debit decreases
 *   INCOME   → Credit increases, Debit decreases
 */
public enum AccountType {
    ASSET,
    LIABILITY,
    EQUITY,
    INCOME,
    EXPENSE
}
