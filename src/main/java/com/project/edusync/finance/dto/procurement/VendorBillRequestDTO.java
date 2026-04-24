package com.project.edusync.finance.dto.procurement;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record VendorBillRequestDTO(
    @NotBlank String vendorInvoiceNumber,
    @NotNull Long vendorId,
    Long purchaseOrderId,
    Long grnId,
    @NotNull LocalDate billDate,
    LocalDate dueDate,
    @NotNull @DecimalMin("0.01") BigDecimal billAmount,
    @DecimalMin("0.00") BigDecimal taxAmount,
    String notes
) {}
