package com.project.edusync.finance.model.enums;

/**
 * Lifecycle status of a Fixed Asset.
 */
public enum AssetStatus {
    /** Purchased but not yet put into use. */
    IN_TRANSIT,
    /** Actively in use. */
    ACTIVE,
    /** Temporarily out of service for repair. */
    UNDER_MAINTENANCE,
    /** Permanently retired / written off. */
    DISPOSED,
    /** Lost or stolen. */
    WRITTEN_OFF
}
