package com.project.edusync.finance.dto.procurement;

import java.math.BigDecimal;

public record POItemResponseDTO(
    Long itemId,
    String description,
    String unitOfMeasure,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal,
    BigDecimal quantityReceived,
    BigDecimal outstandingQuantity,
    boolean fullyReceived,
    Integer lineNumber
) {}
