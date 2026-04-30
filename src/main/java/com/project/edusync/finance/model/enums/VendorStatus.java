package com.project.edusync.finance.model.enums;

/**
 * Lifecycle status of a Vendor record.
 */
public enum VendorStatus {
    /** Application received but not yet verified. */
    PENDING_VERIFICATION,
    /** Verified and active — can receive POs. */
    ACTIVE,
    /** Temporarily suspended — no new POs. */
    SUSPENDED,
    /** Blacklisted — cannot be re-activated without admin override. */
    BLACKLISTED
}
