package com.project.edusync.finance.model.enums;

/**
 * Reconciliation status of a bank statement transaction.
 */
public enum ReconciliationStatus {
    /** Transaction imported from bank statement — not yet matched to a GL entry. */
    UNRECONCILED,
    /** Auto-matched to a GL entry by amount + date proximity. */
    AUTO_MATCHED,
    /** Manually matched by Finance Admin. */
    MANUALLY_MATCHED,
    /** Flagged as an exception — amount/date differs from GL record. */
    EXCEPTION,
    /** Ignored (duplicate import, bank charge, etc.). */
    IGNORED
}
