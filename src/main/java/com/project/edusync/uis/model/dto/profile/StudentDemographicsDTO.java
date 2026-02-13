package com.project.edusync.uis.model.dto.profile;

import com.project.edusync.uis.model.enums.Ethnicity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDemographicsDTO {
    private String nationality;
    private Ethnicity ethnicity;
    private String primaryLanguage;
    private String homeLanguage;
}
