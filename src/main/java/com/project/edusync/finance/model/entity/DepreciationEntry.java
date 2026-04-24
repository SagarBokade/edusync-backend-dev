package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single depreciation charge on a Fixed Asset.
 *
 * Created by the depreciation batch job or manually triggered.
 * GL entry: Dr Depreciation Expense (5xxx), Cr Accumulated Depreciation (1xxx).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "fa_depreciation_entries")
public class DepreciationEntry extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private FixedAsset asset;

    /** Financial year this charge applies to (e.g., "2025-2026"). */
    @Column(name = "financial_year", nullable = false, length = 10)
    private String financialYear;

    @Column(name = "depreciation_date", nullable = false)
    private LocalDate depreciationDate;

    /** Book value at the start of this period. */
    @Column(name = "opening_book_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal openingBookValue;

    /** Charge amount for this period. */
    @Column(name = "depreciation_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal depreciationAmount;

    /** Book value after this charge (openingBookValue - depreciationAmount). */
    @Column(name = "closing_book_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal closingBookValue;

    /** Reference to the GL journal entry that was auto-posted. */
    @Column(name = "gl_entry_id")
    private Long glEntryId;

    @Column(name = "notes", length = 300)
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;
}
