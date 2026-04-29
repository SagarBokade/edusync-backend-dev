package com.project.edusync.finance.model.enums;

/**
 * Lifecycle status of a Journal Entry.
 */
public enum JournalEntryStatus {
    /** Entry created but not yet finalized — can be edited. */
    DRAFT,
    /** Entry has been validated (debits == credits) and committed to the ledger. */
    POSTED,
    /** Entry has been reversed by a mirror contra-entry. */
    REVERSED
}
