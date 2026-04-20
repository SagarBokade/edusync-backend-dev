package com.project.edusync.finance.dto.scholarship;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScholarshipTypeDTO {
    private Long id;
    private String name;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private String eligibilityCriteria;
    private Integer maxRecipients;
    private Integer activeCount;
    private BigDecimal totalDiscountIssued;
}
