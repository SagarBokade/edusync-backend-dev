package com.project.edusync.finance.model.enums;

/**
 * Lifecycle of a Purchase Order.
 *
 * Flow: DRAFT → SUBMITTED → APPROVED → PARTIALLY_RECEIVED / RECEIVED → CLOSED / CANCELLED
 *                         ↘ REJECTED
 */
public enum PurchaseOrderStatus {
    /** PO is being drafted. */
    DRAFT,
    /** Submitted to Finance Admin for approval. */
    SUBMITTED,
    /** Approved — sent to vendor. */
    APPROVED,
    /** Rejected by Finance Admin. */
    REJECTED,
    /** Goods partially received (GRN partially fulfilled). */
    PARTIALLY_RECEIVED,
    /** All goods received (GRN == PO quantities). */
    FULLY_RECEIVED,
    /** PO cancelled before any goods were received. */
    CANCELLED,
    /** Invoice matched and payment complete — final state. */
    CLOSED
}
