package com.project.edusync.finance.dto.installment;

import lombok.Data;

@Data
public class InstallmentPlanDTO {
    private Long id;
    private String name;
    private Integer numberOfInstallments;
    private Integer intervalDays;
    private String description;
    private Integer gracePeriodDays;
    private Integer assignedStudents;
}
