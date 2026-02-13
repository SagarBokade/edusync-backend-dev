package com.project.edusync.uis.model.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkedStudentDTO {
    private Long studentId;
    private String studentName;
    private String enrollmentNo;
    private String relationshipType;
}
