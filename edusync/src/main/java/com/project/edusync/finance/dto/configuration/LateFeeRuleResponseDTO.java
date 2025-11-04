package com.project.edusync.finance.dto.configuration;

import com.project.edusync.finance.model.enums.FineType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Based on finance.late_fee_rules  and /late-fee-rules
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LateFeeRuleResponseDTO {
    private Long ruleId;
    private String ruleName;
    private int daysAfterDue;
    private FineType fineType; // 'FIXED', 'PERCENTAGE'
    private BigDecimal fineValue;
    private boolean isActive;
    private Long structureId; // Nullable
    // Getters and Setters
}
