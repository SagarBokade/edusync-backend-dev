package com.project.edusync.finance.dto.procurement;

import com.project.edusync.finance.model.enums.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderResponseDTO(
    Long id, UUID uuid, String poNumber,
    Long vendorId, String vendorName, String vendorCode,
    String department,
    Long referenceBudgetId, String referenceBudgetName,
    LocalDate orderDate, LocalDate expectedDeliveryDate,
    String description,
    BigDecimal totalBeforeTax, BigDecimal taxAmount, BigDecimal totalAmount,
    PurchaseOrderStatus status,
    String approvedBy, String notes,
    List<POItemResponseDTO> items,
    /** Summary of all GRNs linked to this PO. */
    int grnCount,
    LocalDateTime createdAt, String createdBy
) {}
