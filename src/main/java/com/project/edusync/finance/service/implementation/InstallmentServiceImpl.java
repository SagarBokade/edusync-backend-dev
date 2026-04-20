package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.installment.InstallmentAssignmentCreateDTO;
import com.project.edusync.finance.dto.installment.InstallmentAssignmentDTO;
import com.project.edusync.finance.dto.installment.InstallmentPlanCreateDTO;
import com.project.edusync.finance.dto.installment.InstallmentPlanDTO;
import com.project.edusync.finance.mapper.InstallmentMapper;
import com.project.edusync.finance.model.entity.InstallmentAssignment;
import com.project.edusync.finance.model.entity.InstallmentPlan;
import com.project.edusync.finance.repository.InstallmentAssignmentRepository;
import com.project.edusync.finance.repository.InstallmentPlanRepository;
import com.project.edusync.finance.service.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentServiceImpl implements InstallmentService {

    private final InstallmentPlanRepository planRepository;
    private final InstallmentAssignmentRepository assignmentRepository;
    private final InstallmentMapper mapper;

    @Override
    @Transactional
    public InstallmentPlanDTO createPlan(InstallmentPlanCreateDTO dto) {
        InstallmentPlan plan = InstallmentPlan.builder()
                .name(dto.getName())
                .numberOfInstallments(dto.getNumberOfInstallments())
                .intervalDays(dto.getIntervalDays())
                .description(dto.getDescription())
                .gracePeriodDays(dto.getGracePeriodDays())
                .assignedStudents(0)
                .build();
        return mapper.toDto(planRepository.save(plan));
    }

    @Override
    public List<InstallmentPlanDTO> getAllPlans() {
        return planRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InstallmentAssignmentDTO assignPlan(InstallmentAssignmentCreateDTO dto) {
        InstallmentPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        BigDecimal total = dto.getTotalAmount();
        BigDecimal nextDue = total.divide(BigDecimal.valueOf(plan.getNumberOfInstallments()), 2, RoundingMode.HALF_UP);

        InstallmentAssignment assignment = InstallmentAssignment.builder()
                .studentId(dto.getStudentId())
                .studentName(dto.getStudentName())
                .plan(plan)
                .totalAmount(total)
                .paidInstallments(0)
                .totalInstallments(plan.getNumberOfInstallments())
                .nextDueDate(dto.getNextDueDate())
                .nextDueAmount(nextDue)
                .status("ON_TRACK")
                .build();

        plan.setAssignedStudents(plan.getAssignedStudents() + 1);
        planRepository.save(plan);

        return mapper.toDto(assignmentRepository.save(assignment));
    }

    @Override
    public List<InstallmentAssignmentDTO> getAllAssignments() {
        return assignmentRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
