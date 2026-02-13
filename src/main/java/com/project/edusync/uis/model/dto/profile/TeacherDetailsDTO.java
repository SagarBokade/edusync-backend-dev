package com.project.edusync.uis.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherDetailsDTO {
    private String certifications;
    private String specializations;
    private Integer yearsOfExperience;
    private String educationLevel;
    private String stateLicenseNumber;
}
