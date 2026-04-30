package com.project.edusync.finance.dto.procurement;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record GRNItemRequestDTO(
    @NotNull Long poItemId,
    @NotNull @DecimalMin("0.001") BigDecimal receivedQuantity,
    @NotNull @DecimalMin("0.000") BigDecimal acceptedQuantity,
    BigDecimal rejectedQuantity,
    String rejectionReason,
    String description
) {}
