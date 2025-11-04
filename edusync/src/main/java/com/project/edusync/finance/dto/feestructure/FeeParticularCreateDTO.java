package com.project.edusync.finance.dto.feestructure;

import com.project.edusync.finance.model.enums.Frequency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Used inside FeeStructureCreateDTO
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeParticularCreateDTO {
    @NotNull
    private Long feeTypeId;
    @NotNull
    private String name;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Frequency frequency;
    // Getters and Setters
}