package com.project.edusync.finance.dto.installment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InstallmentPlanCreateDTO {
    @NotBlank
    private String name;
    @NotNull
    private Integer numberOfInstallments;
    @NotNull
    private Integer intervalDays;
    private String description;
    @NotNull
    private Integer gracePeriodDays;
}
