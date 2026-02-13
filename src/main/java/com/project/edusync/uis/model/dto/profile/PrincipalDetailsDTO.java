package com.project.edusync.uis.model.dto.profile;

import com.project.edusync.uis.model.enums.SchoolLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrincipalDetailsDTO {
    private String administrativeCertifications;
    private SchoolLevel schoolLevelManaged;
    private BigDecimal budgetApprovalLimit;
}
