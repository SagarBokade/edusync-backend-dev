package com.project.edusync.finance.model.enums;

/**
 * Depreciation calculation method for fixed assets.
 *
 * SLM  — Straight Line Method: equal depreciation each year
 * WDV  — Written Down Value / Declining Balance: applied on book value
 * UNITS_OF_PRODUCTION — based on actual usage/output
 */
public enum DepreciationMethod {
    STRAIGHT_LINE,
    WRITTEN_DOWN_VALUE,
    UNITS_OF_PRODUCTION
}
