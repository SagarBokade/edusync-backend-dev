package com.project.edusync.finance.dto.configuration;

import com.project.edusync.finance.model.enums.FineType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Used for POST /late-fee-rules
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LateFeeRuleCreateDTO {
    @NotNull
    private String ruleName;
    @NotNull
    private int daysAfterDue;
    @NotNull
    private FineType fineType;
    @NotNull
    private BigDecimal fineValue;
    private boolean isActive = true;
    private Long structureId;
    // Getters and Setters
}
