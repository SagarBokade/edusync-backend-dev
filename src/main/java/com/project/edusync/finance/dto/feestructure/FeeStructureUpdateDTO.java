package com.project.edusync.finance.dto.feestructure;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used for PUT /structures/{structureId}
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeStructureUpdateDTO {
    @NotNull
    private String name;
    @NotNull
    private String academicYear;
    private String description;
    private boolean isActive;
    // Note: Managing particulars (add/remove/update) in a PUT
    // might be handled via dedicated endpoints or a more complex DTO.
    // This DTO assumes updating the main structure's details.
    // Getters and Setters
}
