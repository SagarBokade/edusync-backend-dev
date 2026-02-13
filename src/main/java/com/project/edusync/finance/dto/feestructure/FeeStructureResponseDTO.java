package com.project.edusync.finance.dto.feestructure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// Used for GET /structures and GET /structures/{structureId}
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureResponseDTO {
    private Long structureId;
    private String name;
    private String academicYear;
    private String description;
    private boolean isActive;
    private List<FeeParticularResponseDTO> particulars;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Getters and Setters
}
