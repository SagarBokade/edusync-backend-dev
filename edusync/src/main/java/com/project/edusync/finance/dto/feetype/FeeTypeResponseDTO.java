package com.project.edusync.finance.dto.feetype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used for GET /fee-types
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeTypeResponseDTO {
    private Long feeTypeId;
    private String typeName; // e.g., "TUITION", "TRANSPORT"
    private String description;
    // Getters and Setters
}