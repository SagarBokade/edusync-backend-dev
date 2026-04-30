package com.project.edusync.finance.dto.asset;

import com.project.edusync.finance.model.enums.AssetStatus;
import com.project.edusync.finance.model.enums.DepreciationMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AssetResponseDTO(
    Long id, UUID uuid, String assetCode, String name, String assetCategory,
    String location, String description, String make, String model, String serialNumber,
    LocalDate purchaseDate, LocalDate inUseDate,
    BigDecimal purchaseCost, BigDecimal salvageValue, BigDecimal depreciableAmount,
    Integer usefulLifeYears,
    DepreciationMethod depreciationMethod, BigDecimal depreciationRatePct,
    BigDecimal accumulatedDepreciation, BigDecimal currentBookValue,
    LocalDate lastDepreciationDate,
    AssetStatus status,
    Long vendorId, String vendorName,
    Long assetAccountId, String assetAccountCode, String assetAccountName,
    LocalDate disposalDate, String disposalReason, BigDecimal disposalProceeds,
    String notes,
    LocalDateTime createdAt, String createdBy
) {}
