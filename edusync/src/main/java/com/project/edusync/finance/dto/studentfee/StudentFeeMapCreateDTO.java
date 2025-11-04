package com.project.edusync.finance.dto.studentfee;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Used for POST /student-maps
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeMapCreateDTO {
    @NotNull
    private Long studentId;
    @NotNull
    private Long structureId;
    @NotNull
    private LocalDate effectiveDate;
    private String notes;
    // Getters and Setters
}