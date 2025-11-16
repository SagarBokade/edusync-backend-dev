package com.project.edusync.uis.model.dto.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.edusync.uis.model.enums.StaffType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffProfileDTO {
    private Long staffId;
    private String staffSystemId;
    private String jobTitle;
    private String department;
    private StaffType staffType; // TEACHER, PRINCIPAL, etc.
    // ... other fields from Staff.java
}
