package com.project.edusync.finance.dto.installment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InstallmentAssignmentCreateDTO {
    @NotNull
    private Long studentId;
    @NotBlank
    private String studentName;
    @NotNull
    private Long planId;
    @NotNull
    private BigDecimal totalAmount;
    @NotNull
    private LocalDate nextDueDate;
}
