package com.project.edusync.finance.dto.scholarship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScholarshipAssignmentCreateDTO {
    @NotNull
    private Long studentId;
    @NotBlank
    private String studentName;
    @NotNull
    private Long scholarshipId;
    @NotBlank
    private String reason;
}
