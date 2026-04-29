package com.project.edusync.finance.model.enums;

/**
 * Identifies which source system or event triggered a Journal Entry.
 * Enables drill-through from any GL entry back to its originating document.
 */
public enum JournalReferenceType {
    /** Created from a student fee Payment record. */
    PAYMENT,
    /** Created from a fee Refund record. */
    REFUND,
    /** Created when HRMS finalizes a PayrollRun. */
    PAYROLL_RUN,
    /** Created from a staff ExpenseClaim approval. */
    EXPENSE_CLAIM,
    /** Created from a Vendor Bill payment. */
    VENDOR_BILL,
    /** Created when a Grant utilization is recorded. */
    GRANT_UTILIZATION,
    /** Created by the automated monthly depreciation batch. */
    DEPRECIATION,
    /** Created manually by an accountant via the GL screen. */
    MANUAL
}
