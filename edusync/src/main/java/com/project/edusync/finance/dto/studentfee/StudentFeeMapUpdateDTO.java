package com.project.edusync.finance.dto.studentfee;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Used for PUT /student-maps/{mapId}
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeMapUpdateDTO {
    @NotNull
    private Long studentId;
    @NotNull
    private Long structureId;
    @NotNull
    private LocalDate effectiveDate;
    private String notes;
    // Getters and Setters
}