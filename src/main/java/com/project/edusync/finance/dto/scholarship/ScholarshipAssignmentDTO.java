package com.project.edusync.finance.dto.scholarship;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ScholarshipAssignmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String scholarshipName;
    private Long scholarshipId;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String reason;
    private String status;
}
