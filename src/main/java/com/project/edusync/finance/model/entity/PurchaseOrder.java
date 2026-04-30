package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A Purchase Order issued to a vendor.
 *
 * PO → GRN (goods receipt) → VendorBill (invoice) = 3-Way Match
 *
 * A PO must be APPROVED before goods are received.
 * Once all GRN items match the PO quantities, status becomes FULLY_RECEIVED.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proc_purchase_orders")
public class PurchaseOrder extends AuditableEntity {

    /** Auto-generated human-readable number like "PO-2025-001234". */
    @Column(name = "po_number", nullable = false, unique = true, length = 30)
    private String poNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    /** Department raising this PO (e.g., "CSE Lab", "Library"). */
    @Column(name = "department", length = 150)
    private String department;

    /** Optional link to the approved budget this PO is drawing from. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    private Budget referenceBudget;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /** Expected delivery date from vendor. */
    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Tax amounts (GST). */
    @Column(name = "total_before_tax", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal totalBeforeTax = BigDecimal.ZERO;

    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    /** total_before_tax + tax_amount. */
    @Column(name = "total_amount", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'DRAFT'")
    private PurchaseOrderStatus status = PurchaseOrderStatus.DRAFT;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PurchaseOrderItem> items = new ArrayList<>();

    /** Linked purchase receipts (GRNs). */
    @OneToMany(mappedBy = "purchaseOrder", fetch = FetchType.LAZY)
    private List<GoodsReceiptNote> grns = new ArrayList<>();

    public void addItem(PurchaseOrderItem item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }

    /** Recalculates totals from line items. */
    public void recalculateTotals(BigDecimal gstPct) {
        this.totalBeforeTax = items.stream()
                .map(i -> i.getUnitPrice().multiply(i.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.taxAmount = this.totalBeforeTax.multiply(gstPct).divide(BigDecimal.valueOf(100));
        this.totalAmount = this.totalBeforeTax.add(this.taxAmount);
    }
}
