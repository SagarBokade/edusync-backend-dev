package com.project.edusync.finance.dto.procurement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderRequestDTO(
    @NotNull(message = "Vendor ID is required") Long vendorId,
    String department,
    Long referenceBudgetId,
    @NotNull LocalDate orderDate,
    LocalDate expectedDeliveryDate,
    String description,
    @DecimalMin("0.00") BigDecimal gstPercentage,
    String notes,
    @NotEmpty @Valid List<POItemRequestDTO> items
) {}
