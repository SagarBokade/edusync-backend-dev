package com.project.edusync.finance.dto.grant;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GrantUtilizationRequestDTO(
    @NotNull Long grantId,
    @NotNull LocalDate utilisationDate,
    @NotBlank String description,
    String expenseCategory,
    @NotNull @DecimalMin("0.01") BigDecimal amount,
    String referenceDocument,
    String notes
) {}
