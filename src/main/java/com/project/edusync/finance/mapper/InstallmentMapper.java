package com.project.edusync.finance.mapper;

import com.project.edusync.finance.dto.installment.InstallmentAssignmentDTO;
import com.project.edusync.finance.dto.installment.InstallmentPlanDTO;
import com.project.edusync.finance.model.entity.InstallmentAssignment;
import com.project.edusync.finance.model.entity.InstallmentPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InstallmentMapper {
    InstallmentPlanDTO toDto(InstallmentPlan plan);
    
    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planName", source = "plan.name")
    InstallmentAssignmentDTO toDto(InstallmentAssignment assignment);
}
