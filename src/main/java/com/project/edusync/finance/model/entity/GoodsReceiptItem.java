package com.project.edusync.finance.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

/**
 * A single item line within a Goods Receipt Note.
 *
 * Linked to an originating PurchaseOrderItem.
 * acceptedQuantity is used for the 3-way match against the VendorBill.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proc_grn_items")
public class GoodsReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grn_item_id")
    private Long grnItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id", nullable = false)
    private GoodsReceiptNote goodsReceiptNote;

    /** The PO item this receipt line is fulfilling. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;

    @Column(name = "description", length = 300)
    private String description;

    /** Quantity physically delivered. */
    @Column(name = "received_quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal receivedQuantity;

    /** Quantity confirmed good after inspection (≤ receivedQuantity). */
    @Column(name = "accepted_quantity", nullable = false, precision = 10, scale = 3)
    @ColumnDefault("0.000")
    private BigDecimal acceptedQuantity = BigDecimal.ZERO;

    /** Quantity rejected / returned. */
    @Column(name = "rejected_quantity", nullable = false, precision = 10, scale = 3)
    @ColumnDefault("0.000")
    private BigDecimal rejectedQuantity = BigDecimal.ZERO;

    @Column(name = "rejection_reason", length = 300)
    private String rejectionReason;

    @Column(name = "line_number")
    private Integer lineNumber;
}
