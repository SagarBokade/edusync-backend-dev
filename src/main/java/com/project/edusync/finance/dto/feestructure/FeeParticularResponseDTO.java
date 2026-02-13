package com.project.edusync.finance.dto.feestructure;

import com.project.edusync.finance.model.enums.Frequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Based on finance.fee_particulars
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeParticularResponseDTO {
    private Long particularId;
    private Long feeTypeId; // Foreign Key to finance.fee_types
    private String name;
    private BigDecimal amount;
    private Frequency frequency; // e.g., 'one_time', 'monthly'
    // Getters and Setters
}