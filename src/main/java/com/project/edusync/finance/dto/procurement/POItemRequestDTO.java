package com.project.edusync.finance.dto.procurement;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record POItemRequestDTO(
    @NotBlank String description,
    String unitOfMeasure,
    @NotNull @DecimalMin("0.001") BigDecimal quantity,
    @NotNull @DecimalMin("0.01") BigDecimal unitPrice
) {}
