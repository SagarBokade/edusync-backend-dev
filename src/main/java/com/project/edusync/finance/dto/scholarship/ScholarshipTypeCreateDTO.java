package com.project.edusync.finance.dto.scholarship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScholarshipTypeCreateDTO {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String discountType;
    @NotNull
    private BigDecimal discountValue;
    private String eligibilityCriteria;
    private Integer maxRecipients;
}
