package com.project.edusync.finance.model.enums;

/**
 * Status of an Accounts Payable Vendor Bill.
 *
 * Flow: PENDING → THREE_WAY_MATCHED → APPROVED_FOR_PAYMENT → PAID → CANCELLED
 *               ↘ MISMATCH (auto-flagged when PO/GRN/Bill quantities don't align)
 */
public enum VendorBillStatus {
    /** Bill received, awaiting 3-way match verification. */
    PENDING,
    /**
     * 3-way match PASSED: PO qty == GRN accepted qty == Bill qty.
     * Ready for Finance Admin payment approval.
     */
    THREE_WAY_MATCHED,
    /**
     * 3-way match FAILED: quantities don't align.
     * Requires manual intervention before payment can proceed.
     */
    MISMATCH,
    /** Finance Admin approved — payment instruction sent. */
    APPROVED_FOR_PAYMENT,
    /** Payment disbursed — GL entry posted (Debit: AP, Credit: Bank). */
    PAID,
    /** Bill voided (wrong invoice, duplicate, etc.). */
    CANCELLED
}
