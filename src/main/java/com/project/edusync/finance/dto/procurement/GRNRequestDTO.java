package com.project.edusync.finance.dto.procurement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record GRNRequestDTO(
    @NotNull Long purchaseOrderId,
    @NotNull LocalDate receiptDate,
    String receivedBy,
    String vendorChallanNumber,
    String notes,
    @NotEmpty @Valid List<GRNItemRequestDTO> items
) {}
