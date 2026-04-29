package com.project.edusync.finance.model.enums;

/**
 * Lifecycle status of a Department Budget.
 *
 * Flow: DRAFT → SUBMITTED → APPROVED ↔ REVISION_REQUESTED → CLOSED
 *                         ↘ REJECTED
 */
public enum BudgetStatus {
    /** Budget is being drafted by an HOD or Finance Admin. */
    DRAFT,
    /** Submitted for approval — read-only until reviewed. */
    SUBMITTED,
    /** Approved by Finance Admin — amounts are now binding. */
    APPROVED,
    /** Reviewer requested changes — returns to the submitter. */
    REVISION_REQUESTED,
    /** Rejected outright. */
    REJECTED,
    /** Academic year ended — budget is archived. */
    CLOSED
}
