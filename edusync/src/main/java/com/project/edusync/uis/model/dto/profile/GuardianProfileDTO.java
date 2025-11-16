package com.project.edusync.uis.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuardianProfileDTO {
    private Long guardianId;
    private String occupation;
    private String employer;
    // ... other fields from Guardian.java

    // This list will show the students this guardian is responsible for
    private List<LinkedStudentDTO> linkedStudents;
}
