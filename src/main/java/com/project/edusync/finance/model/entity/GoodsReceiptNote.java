package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.GRNStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Goods Receipt Note (GRN) — Step 2 of the 3-Way Match.
 *
 * Created when goods ordered on a PO physically arrive at the institution.
 * The storekeeper confirms each item's received quantity and condition.
 * Once all items are ACCEPTED, the PO status updates to FULLY_RECEIVED or PARTIALLY_RECEIVED.
 *
 * 3-Way Match: PO → GRN → VendorBill
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proc_grns")
public class GoodsReceiptNote extends AuditableEntity {

    /** Auto-generated number like "GRN-2025-001234". */
    @Column(name = "grn_number", nullable = false, unique = true, length = 30)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    /** Name of the person who received/inspected the goods. */
    @Column(name = "received_by", length = 100)
    private String receivedBy;

    /** Vendor's delivery/challan number from their dispatch document. */
    @Column(name = "vendor_challan_number", length = 50)
    private String vendorChallanNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'PENDING_INSPECTION'")
    private GRNStatus status = GRNStatus.PENDING_INSPECTION;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;

    @OneToMany(mappedBy = "goodsReceiptNote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GoodsReceiptItem> items = new ArrayList<>();

    public void addItem(GoodsReceiptItem item) {
        items.add(item);
        item.setGoodsReceiptNote(this);
    }
}
