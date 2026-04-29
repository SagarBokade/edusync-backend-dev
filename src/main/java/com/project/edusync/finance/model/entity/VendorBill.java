package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.VendorBillStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Vendor Bill (Accounts Payable Invoice) — Step 3 and final step of the 3-Way Match.
 *
 * When a vendor submits their tax invoice after delivering goods:
 *  1. Finance creates a VendorBill linked to the PO and its GRN.
 *  2. The system automatically runs 3-Way Match:
 *       PO qty == GRN accepted qty == Bill qty → status = THREE_WAY_MATCHED
 *       Any mismatch   → status = MISMATCH (flagged for manual review)
 *  3. Matched bills are APPROVED_FOR_PAYMENT by Finance Admin.
 *  4. On payment: GL entry posted (Dr: Accounts Payable 2110, Cr: Bank 1120).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proc_vendor_bills")
public class VendorBill extends AuditableEntity {

    /** Vendor's own invoice number (must be unique per vendor). */
    @Column(name = "vendor_invoice_number", nullable = false, length = 50)
    private String vendorInvoiceNumber;

    /** Internal system reference like "BILL-2025-001234". */
    @Column(name = "bill_number", nullable = false, unique = true, length = 30)
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id")
    private GoodsReceiptNote goodsReceiptNote;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "bill_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal billAmount;

    @Column(name = "tax_amount", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_payable", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalPayable;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'PENDING'")
    private VendorBillStatus status = VendorBillStatus.PENDING;

    /**
     * JSON-serializable result of the 3-way match check.
     * Stores human-readable discrepancy details for MISMATCH bills.
     */
    @Column(name = "match_result_notes", columnDefinition = "TEXT")
    private String matchResultNotes;

    /** Set when the match passed — ensures auditability. */
    @Column(name = "matched_by", length = 100)
    private String matchedBy;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    /** GL entry ID created when the bill was paid. */
    @Column(name = "gl_entry_id")
    private Long glEntryId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;
}
