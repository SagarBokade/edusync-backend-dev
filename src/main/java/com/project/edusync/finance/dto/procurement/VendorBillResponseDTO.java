package com.project.edusync.finance.dto.procurement;

import com.project.edusync.finance.model.enums.VendorBillStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VendorBillResponseDTO(
    Long id, UUID uuid,
    String billNumber, String vendorInvoiceNumber,
    Long vendorId, String vendorName, String vendorCode,
    Long purchaseOrderId, String poNumber,
    Long grnId, String grnNumber,
    LocalDate billDate, LocalDate dueDate,
    BigDecimal billAmount, BigDecimal taxAmount, BigDecimal totalPayable,
    VendorBillStatus status,
    String matchResultNotes, String matchedBy,
    LocalDate paymentDate, String paymentReference,
    Long glEntryId,
    String notes,
    boolean overdue,
    LocalDateTime createdAt, String createdBy
) {}
