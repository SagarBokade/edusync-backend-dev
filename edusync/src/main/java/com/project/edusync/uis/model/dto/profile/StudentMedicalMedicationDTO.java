package com.project.edusync.uis.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentMedicalMedicationDTO {
    private String medicationName;
    private String dosage;
    private String frequency;
}
