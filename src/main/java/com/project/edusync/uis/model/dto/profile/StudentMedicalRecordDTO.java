package com.project.edusync.uis.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentMedicalRecordDTO {
    private Long id;
    private String physicianName;
    private String physicianPhone;
    private String insuranceProvider;
    private String insurancePolicyNumber;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private List<StudentMedicalAllergyDTO> allergies;
    private List<StudentMedicalMedicationDTO> medications;
}
