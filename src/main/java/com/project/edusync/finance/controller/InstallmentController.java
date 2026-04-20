package com.project.edusync.finance.controller;

import com.project.edusync.finance.dto.installment.InstallmentAssignmentCreateDTO;
import com.project.edusync.finance.dto.installment.InstallmentAssignmentDTO;
import com.project.edusync.finance.dto.installment.InstallmentPlanCreateDTO;
import com.project.edusync.finance.dto.installment.InstallmentPlanDTO;
import com.project.edusync.finance.service.InstallmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.url}/auth/finance/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentService installmentService;

    @PostMapping("/plans")
    public ResponseEntity<InstallmentPlanDTO> createPlan(@RequestBody @Valid InstallmentPlanCreateDTO dto) {
        return ResponseEntity.ok(installmentService.createPlan(dto));
    }

    @GetMapping("/plans")
    public ResponseEntity<List<InstallmentPlanDTO>> getAllPlans() {
        return ResponseEntity.ok(installmentService.getAllPlans());
    }

    @PostMapping("/assignments")
    public ResponseEntity<InstallmentAssignmentDTO> assignPlan(@RequestBody @Valid InstallmentAssignmentCreateDTO dto) {
        return ResponseEntity.ok(installmentService.assignPlan(dto));
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<InstallmentAssignmentDTO>> getAllAssignments() {
        return ResponseEntity.ok(installmentService.getAllAssignments());
    }
}
