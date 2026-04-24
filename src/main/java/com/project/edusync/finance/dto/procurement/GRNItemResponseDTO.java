package com.project.edusync.finance.dto.procurement;

import java.math.BigDecimal;

public record GRNItemResponseDTO(
    Long grnItemId,
    Long poItemId,
    String description,
    BigDecimal receivedQuantity,
    BigDecimal acceptedQuantity,
    BigDecimal rejectedQuantity,
    String rejectionReason,
    Integer lineNumber
) {}
