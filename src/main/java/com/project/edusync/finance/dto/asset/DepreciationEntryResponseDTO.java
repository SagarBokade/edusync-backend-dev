package com.project.edusync.finance.dto.asset;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DepreciationEntryResponseDTO(
    Long id, Long assetId, String assetCode, String assetName,
    String financialYear, LocalDate depreciationDate,
    BigDecimal openingBookValue, BigDecimal depreciationAmount, BigDecimal closingBookValue,
    Long glEntryId, String notes,
    LocalDateTime createdAt, String createdBy
) {}
