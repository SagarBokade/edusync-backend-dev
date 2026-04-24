package com.project.edusync.finance.dto.procurement;

import com.project.edusync.finance.model.enums.GRNStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GRNResponseDTO(
    Long id, UUID uuid, String grnNumber,
    Long purchaseOrderId, String poNumber,
    Long vendorId, String vendorName,
    LocalDate receiptDate, String receivedBy,
    String vendorChallanNumber,
    GRNStatus status, String notes,
    List<GRNItemResponseDTO> items,
    LocalDateTime createdAt, String createdBy
) {}
