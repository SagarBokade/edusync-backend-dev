package com.project.edusync.finance.dto.grant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record GrantUtilizationResponseDTO(
    Long id, Long grantId, String grantTitle,
    LocalDate utilisationDate, String description, String expenseCategory,
    BigDecimal amount, String referenceDocument,
    Long glEntryId, String approvedBy, String notes,
    LocalDateTime createdAt
) {}
