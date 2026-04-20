package com.project.edusync.finance.dto.installment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InstallmentAssignmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String planName;
    private Long planId;
    private BigDecimal totalAmount;
    private Integer paidInstallments;
    private Integer totalInstallments;
    private LocalDate nextDueDate;
    private BigDecimal nextDueAmount;
    private String status;
}
