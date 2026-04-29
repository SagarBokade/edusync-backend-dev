package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.AssetStatus;
import com.project.edusync.finance.model.enums.DepreciationMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Fixed Asset — equipment, furniture, vehicles, buildings, etc.
 *
 * Asset lifecycle:
 *   Purchase → IN_TRANSIT → ACTIVE → (UNDER_MAINTENANCE) → DISPOSED / WRITTEN_OFF
 *
 * Depreciation can be calculated on demand (yearly batch) or per request.
 * The currentBookValue is updated after each depreciation run.
 *
 * GL entries on events:
 *   Purchase:  Dr Fixed Asset Account, Cr Bank/AP
 *   Depreciation: Dr Depreciation Expense, Cr Accumulated Depreciation
 *   Disposal:  Dr Accumulated Depreciation + Dr Loss (if any), Cr Fixed Asset Account
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "fa_fixed_assets")
public class FixedAsset extends AuditableEntity {

    /** System-generated: "ASSET-2025-00123". */
    @Column(name = "asset_code", nullable = false, unique = true, length = 30)
    private String assetCode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** Type: Furniture, IT Equipment, Lab Equipment, Vehicle, Building, etc. */
    @Column(name = "asset_category", length = 100)
    private String assetCategory;

    /** Department/location where the asset is deployed. */
    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Manufacturer/brand. */
    @Column(name = "make", length = 100)
    private String make;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    /** Date the asset was physically purchased. */
    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    /** Date the asset was put into use (depreciation starts from here). */
    @Column(name = "in_use_date")
    private LocalDate inUseDate;

    /** Gross cost at time of purchase. */
    @Column(name = "purchase_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal purchaseCost;

    /** Estimated salvage/scrap value at end of useful life. */
    @Column(name = "salvage_value", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal salvageValue = BigDecimal.ZERO;

    /** Expected useful life in years. */
    @Column(name = "useful_life_years", nullable = false)
    private Integer usefulLifeYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "depreciation_method", nullable = false, length = 30)
    @ColumnDefault("'STRAIGHT_LINE'")
    private DepreciationMethod depreciationMethod = DepreciationMethod.STRAIGHT_LINE;

    /** Rate % for WDV method. For SLM, computed from useful life. */
    @Column(name = "depreciation_rate_pct", precision = 6, scale = 3)
    private BigDecimal depreciationRatePct;

    /** Total accumulated depreciation posted so far. */
    @Column(name = "accumulated_depreciation", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal accumulatedDepreciation = BigDecimal.ZERO;

    /** purchaseCost - accumulatedDepreciation. Updated after each depreciation run. */
    @Column(name = "current_book_value", nullable = false, precision = 14, scale = 2)
    private BigDecimal currentBookValue;

    /** Date of last depreciation run. */
    @Column(name = "last_depreciation_date")
    private LocalDate lastDepreciationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'IN_TRANSIT'")
    private AssetStatus status = AssetStatus.IN_TRANSIT;

    /** Linked Vendor from Procurement module — who sold this asset. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    /** COA account for this asset type (e.g., "1310 — Lab Equipment"). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_account_id")
    private Account assetAccount;

    /** GL entry id when asset was purchased. */
    @Column(name = "purchase_gl_entry_id")
    private Long purchaseGlEntryId;

    /** Date/reason for disposal. */
    @Column(name = "disposal_date")
    private LocalDate disposalDate;

    @Column(name = "disposal_reason", length = 300)
    private String disposalReason;

    @Column(name = "disposal_proceeds", precision = 14, scale = 2)
    private BigDecimal disposalProceeds;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;

    /** Convenience: Net Depreciable Amount = purchaseCost - salvageValue. */
    public BigDecimal getDepreciableAmount() {
        return purchaseCost.subtract(salvageValue);
    }
}
