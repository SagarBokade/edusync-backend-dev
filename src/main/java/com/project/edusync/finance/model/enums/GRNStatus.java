package com.project.edusync.finance.model.enums;

/**
 * Status of a Goods Receipt Note (GRN).
 */
public enum GRNStatus {
    /** GRN created and goods physically checked — pending quality inspection. */
    PENDING_INSPECTION,
    /** Quality approved — quantities confirmed against PO. */
    ACCEPTED,
    /** Goods rejected (wrong items, damaged, quantity mismatch). */
    REJECTED,
    /** Partially accepted (some items OK, some rejected). */
    PARTIALLY_ACCEPTED
}
