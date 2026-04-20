package com.project.edusync.finance.service;

import com.project.edusync.finance.dto.installment.InstallmentAssignmentCreateDTO;
import com.project.edusync.finance.dto.installment.InstallmentAssignmentDTO;
import com.project.edusync.finance.dto.installment.InstallmentPlanCreateDTO;
import com.project.edusync.finance.dto.installment.InstallmentPlanDTO;

import java.util.List;

public interface InstallmentService {
    InstallmentPlanDTO createPlan(InstallmentPlanCreateDTO dto);
    List<InstallmentPlanDTO> getAllPlans();
    
    InstallmentAssignmentDTO assignPlan(InstallmentAssignmentCreateDTO dto);
    List<InstallmentAssignmentDTO> getAllAssignments();
}
