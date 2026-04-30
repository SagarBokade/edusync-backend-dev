package com.project.edusync.finance.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * A single line item within a Purchase Order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proc_po_items")
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    @Column(name = "unit_of_measure", length = 30)
    private String unitOfMeasure;

    @Column(name = "quantity", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitPrice;

    /** quantity × unit_price */
    @Column(name = "line_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal lineTotal;

    /**
     * Quantity already received across all GRNs for this PO item.
     * Updated every time a GRN line is accepted.
     */
    @Column(name = "quantity_received", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantityReceived = BigDecimal.ZERO;

    @Column(name = "line_number")
    private Integer lineNumber;

    public BigDecimal getOutstandingQuantity() {
        return quantity.subtract(quantityReceived);
    }

    public boolean isFullyReceived() {
        return quantityReceived.compareTo(quantity) >= 0;
    }
}
