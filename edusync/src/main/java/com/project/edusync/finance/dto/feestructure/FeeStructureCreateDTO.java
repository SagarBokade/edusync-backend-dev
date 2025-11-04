package com.project.edusync.finance.dto.feestructure;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Used for POST /structures
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeStructureCreateDTO {
    @NotNull
    private String name;
    @NotNull
    private String academicYear;
    private String description;
    private boolean isActive = false;
    private List<FeeParticularCreateDTO> particulars;
    // Getters and Setters
}
