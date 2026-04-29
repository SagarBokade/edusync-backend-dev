package com.project.edusync.finance.model.enums;

/**
 * Lifecycle status of a Grant.
 *
 * Flow: APPLIED → SANCTIONED → ACTIVE → PARTIALLY_UTILISED → FULLY_UTILISED → CLOSED/LAPSED
 */
public enum GrantStatus {
    /** Application submitted but not yet approved. */
    APPLIED,
    /** Grant sanctioned (approved) — amount confirmed. */
    SANCTIONED,
    /** Grant funds received — utilisation can begin. */
    ACTIVE,
    /** Some funds have been utilised. */
    PARTIALLY_UTILISED,
    /** All grant funds utilised. Pending compliance submission. */
    FULLY_UTILISED,
    /** Compliance submitted — grant formally closed. */
    CLOSED,
    /** Grant expired without full utilisation — unused amount lapses. */
    LAPSED
}
