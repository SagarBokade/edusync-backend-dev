package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.budget.*;
import com.project.edusync.finance.model.entity.Account;
import com.project.edusync.finance.model.entity.Budget;
import com.project.edusync.finance.model.entity.BudgetLineItem;
import com.project.edusync.finance.model.enums.BudgetStatus;
import com.project.edusync.finance.repository.AccountRepository;
import com.project.edusync.finance.repository.BudgetLineItemRepository;
import com.project.edusync.finance.repository.BudgetRepository;
import com.project.edusync.finance.service.BudgetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetLineItemRepository lineItemRepository;
    private final AccountRepository accountRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Override
    public BudgetResponseDTO createBudget(BudgetCreateDTO dto, Long schoolId) {
        if (budgetRepository.existsByDepartmentNameAndAcademicYearAndSchoolId(
                dto.departmentName(), dto.academicYear(), schoolId)) {
            throw new IllegalArgumentException(
                "A budget for department '" + dto.departmentName() + "' already exists for " + dto.academicYear() + ".");
        }

        Budget budget = new Budget();
        budget.setDepartmentName(dto.departmentName());
        budget.setAcademicYear(dto.academicYear());
        budget.setTitle(dto.title());
        budget.setSubmitterNotes(dto.submitterNotes());
        budget.setStatus(BudgetStatus.DRAFT);
        budget.setSchoolId(schoolId);
        budget.setTotalAllocated(BigDecimal.ZERO);
        budget.setTotalSpent(BigDecimal.ZERO);

        Budget saved = budgetRepository.save(budget);
        buildAndAttachLineItems(saved, dto.lineItems(), schoolId);
        saved.recalculateTotals();
        return toResponseDTO(budgetRepository.save(saved));
    }

    // ── Update (DRAFT only) ───────────────────────────────────────────────────

    @Override
    public BudgetResponseDTO updateBudget(Long budgetId, BudgetCreateDTO dto, Long schoolId) {
        Budget budget = findBudget(budgetId, schoolId);
        if (budget.getStatus() != BudgetStatus.DRAFT && budget.getStatus() != BudgetStatus.REVISION_REQUESTED) {
            throw new IllegalStateException("Only DRAFT or REVISION_REQUESTED budgets can be edited. Current status: " + budget.getStatus());
        }

        budget.setDepartmentName(dto.departmentName());
        budget.setAcademicYear(dto.academicYear());
        budget.setTitle(dto.title());
        budget.setSubmitterNotes(dto.submitterNotes());

        // Replace all line items
        budget.getLineItems().clear();
        buildAndAttachLineItems(budget, dto.lineItems(), schoolId);
        budget.recalculateTotals();
        return toResponseDTO(budgetRepository.save(budget));
    }

    // ── Submit ────────────────────────────────────────────────────────────────

    @Override
    public BudgetResponseDTO submitBudget(Long budgetId, Long schoolId) {
        Budget budget = findBudget(budgetId, schoolId);
        if (budget.getStatus() != BudgetStatus.DRAFT && budget.getStatus() != BudgetStatus.REVISION_REQUESTED) {
            throw new IllegalStateException("Only DRAFT or REVISION_REQUESTED budgets can be submitted.");
        }
        if (budget.getLineItems().isEmpty()) {
            throw new IllegalStateException("Cannot submit a budget with no line items.");
        }
        budget.setStatus(BudgetStatus.SUBMITTED);
        return toResponseDTO(budgetRepository.save(budget));
    }

    // ── Review (Approve / Reject) ─────────────────────────────────────────────

    @Override
    public BudgetResponseDTO reviewBudget(Long budgetId, BudgetApprovalDTO dto, Long schoolId) {
        Budget budget = findBudget(budgetId, schoolId);
        if (budget.getStatus() != BudgetStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED budgets can be reviewed. Current status: " + budget.getStatus());
        }
        String reviewer = getCurrentUsername();
        budget.setApprovedBy(reviewer);
        budget.setReviewerNotes(dto.reviewerNotes());
        budget.setStatus(dto.approved() ? BudgetStatus.APPROVED : BudgetStatus.REJECTED);
        log.info("Budget #{} {} by {}", budgetId, budget.getStatus(), reviewer);
        return toResponseDTO(budgetRepository.save(budget));
    }

    // ── Request Revision ──────────────────────────────────────────────────────

    @Override
    public BudgetResponseDTO requestRevision(Long budgetId, String notes, Long schoolId) {
        Budget budget = findBudget(budgetId, schoolId);
        if (budget.getStatus() != BudgetStatus.SUBMITTED) {
            throw new IllegalStateException("Only SUBMITTED budgets can be sent for revision.");
        }
        budget.setStatus(BudgetStatus.REVISION_REQUESTED);
        budget.setReviewerNotes(notes);
        budget.setApprovedBy(getCurrentUsername());
        return toResponseDTO(budgetRepository.save(budget));
    }

    // ── Close ─────────────────────────────────────────────────────────────────

    @Override
    public BudgetResponseDTO closeBudget(Long budgetId, Long schoolId) {
        Budget budget = findBudget(budgetId, schoolId);
        if (budget.getStatus() != BudgetStatus.APPROVED) {
            throw new IllegalStateException("Only APPROVED budgets can be closed.");
        }
        budget.setStatus(BudgetStatus.CLOSED);
        return toResponseDTO(budgetRepository.save(budget));
    }

    // ── Reads ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<BudgetSummaryDTO> getAllBudgets(Long schoolId) {
        return budgetRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId)
                .stream().map(this::toSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetSummaryDTO> getBudgetsByYear(String academicYear, Long schoolId) {
        return budgetRepository.findBySchoolIdAndAcademicYearOrderByDepartmentNameAsc(schoolId, academicYear)
                .stream().map(this::toSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetSummaryDTO> getBudgetsByStatus(BudgetStatus status, Long schoolId) {
        return budgetRepository.findBySchoolIdAndStatusOrderByCreatedAtDesc(schoolId, status)
                .stream().map(this::toSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BudgetResponseDTO getBudgetById(Long budgetId, Long schoolId) {
        return toResponseDTO(findBudget(budgetId, schoolId));
    }

    @Override
    public void deleteBudget(Long budgetId, Long schoolId) {
        Budget budget = findBudget(budgetId, schoolId);
        if (budget.getStatus() != BudgetStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT budgets can be deleted.");
        }
        budgetRepository.delete(budget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAcademicYears(Long schoolId) {
        return budgetRepository.findDistinctAcademicYears(schoolId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDepartments(Long schoolId) {
        return budgetRepository.findDistinctDepartments(schoolId);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private void buildAndAttachLineItems(Budget budget, List<BudgetLineItemCreateDTO> dtos, Long schoolId) {
        int lineNum = 1;
        for (BudgetLineItemCreateDTO dto : dtos) {
            BudgetLineItem item = new BudgetLineItem();
            item.setCategory(dto.category());
            item.setAllocatedAmount(dto.allocatedAmount());
            item.setActualAmount(BigDecimal.ZERO);
            item.setVariance(dto.allocatedAmount());
            item.setNotes(dto.notes());
            item.setLineNumber(lineNum++);

            if (dto.linkedAccountId() != null) {
                Account acc = accountRepository.findById(dto.linkedAccountId())
                        .orElseThrow(() -> new EntityNotFoundException("Account not found: " + dto.linkedAccountId()));
                item.setLinkedAccount(acc);
                // Seed actual from current account balance
                item.setActualAmount(acc.getBalance().abs());
                item.recalcVariance();
            }

            budget.addLineItem(item);
        }
    }

    private Budget findBudget(Long id, Long schoolId) {
        Budget b = budgetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found: " + id));
        if (!schoolId.equals(b.getSchoolId())) {
            throw new EntityNotFoundException("Budget not found: " + id);
        }
        return b;
    }

    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }

    private double calcUtilisation(BigDecimal spent, BigDecimal allocated) {
        if (allocated == null || allocated.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return spent.multiply(BigDecimal.valueOf(100))
                .divide(allocated, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String trafficLight(double utilPct) {
        if (utilPct > 100) return "OVER";
        if (utilPct >= 75) return "ON_TRACK";
        return "UNDER";
    }

    private BudgetLineItemResponseDTO toLineItemDTO(BudgetLineItem item) {
        double util = calcUtilisation(item.getActualAmount(), item.getAllocatedAmount());
        return new BudgetLineItemResponseDTO(
                item.getLineItemId(),
                item.getCategory(),
                item.getLinkedAccount() != null ? item.getLinkedAccount().getId() : null,
                item.getLinkedAccount() != null ? item.getLinkedAccount().getCode() : null,
                item.getLinkedAccount() != null ? item.getLinkedAccount().getName() : null,
                item.getAllocatedAmount(),
                item.getActualAmount(),
                item.getVariance(),
                util,
                trafficLight(util),
                item.getNotes(),
                item.getLineNumber()
        );
    }

    private BudgetResponseDTO toResponseDTO(Budget budget) {
        List<BudgetLineItemResponseDTO> lines = budget.getLineItems().stream()
                .sorted((a, b) -> {
                    if (a.getLineNumber() == null || b.getLineNumber() == null) return 0;
                    return a.getLineNumber().compareTo(b.getLineNumber());
                })
                .map(this::toLineItemDTO)
                .collect(Collectors.toList());

        BigDecimal totalVariance = budget.getTotalAllocated().subtract(budget.getTotalSpent());
        double util = calcUtilisation(budget.getTotalSpent(), budget.getTotalAllocated());

        return new BudgetResponseDTO(
                budget.getId(),
                budget.getUuid(),
                budget.getDepartmentName(),
                budget.getAcademicYear(),
                budget.getTitle(),
                budget.getTotalAllocated(),
                budget.getTotalSpent(),
                totalVariance,
                util,
                budget.getStatus(),
                budget.getApprovedBy(),
                budget.getReviewerNotes(),
                budget.getSubmitterNotes(),
                lines,
                budget.getCreatedAt(),
                budget.getCreatedBy(),
                budget.getUpdatedAt()
        );
    }

    private BudgetSummaryDTO toSummaryDTO(Budget budget) {
        BigDecimal totalVariance = budget.getTotalAllocated().subtract(budget.getTotalSpent());
        double util = calcUtilisation(budget.getTotalSpent(), budget.getTotalAllocated());
        return new BudgetSummaryDTO(
                budget.getId(),
                budget.getDepartmentName(),
                budget.getAcademicYear(),
                budget.getTitle(),
                budget.getTotalAllocated(),
                budget.getTotalSpent(),
                totalVariance,
                util,
                budget.getStatus(),
                budget.getCreatedBy()
        );
    }
}
