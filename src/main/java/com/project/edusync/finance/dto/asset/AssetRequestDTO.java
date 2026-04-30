package com.project.edusync.finance.dto.asset;

import com.project.edusync.finance.model.enums.AssetStatus;
import com.project.edusync.finance.model.enums.DepreciationMethod;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AssetRequestDTO(
    @NotBlank String name,
    String assetCategory,
    String location,
    String description,
    String make,
    String model,
    String serialNumber,
    @NotNull LocalDate purchaseDate,
    LocalDate inUseDate,
    @NotNull @DecimalMin("0.01") BigDecimal purchaseCost,
    BigDecimal salvageValue,
    @NotNull @Min(1) Integer usefulLifeYears,
    DepreciationMethod depreciationMethod,
    BigDecimal depreciationRatePct,
    Long vendorId,
    Long assetAccountId,
    AssetStatus status,
    String notes
) {}
