package com.project.edusync.finance.service;

import com.project.edusync.finance.dto.budget.*;
import com.project.edusync.finance.model.enums.BudgetStatus;

import java.util.List;

public interface BudgetService {

    /** Create a new budget in DRAFT state. */
    BudgetResponseDTO createBudget(BudgetCreateDTO dto, Long schoolId);

    /** Update a DRAFT budget (replaces all line items). */
    BudgetResponseDTO updateBudget(Long budgetId, BudgetCreateDTO dto, Long schoolId);

    /** Submit a DRAFT budget for Finance Admin review. */
    BudgetResponseDTO submitBudget(Long budgetId, Long schoolId);

    /** Finance Admin approves or rejects a SUBMITTED budget. */
    BudgetResponseDTO reviewBudget(Long budgetId, BudgetApprovalDTO dto, Long schoolId);

    /** Request a revision — returns budget to REVISION_REQUESTED status. */
    BudgetResponseDTO requestRevision(Long budgetId, String notes, Long schoolId);

    /** Close a budget at the end of an academic year. */
    BudgetResponseDTO closeBudget(Long budgetId, Long schoolId);

    /** Get all budgets for a school (summary list). */
    List<BudgetSummaryDTO> getAllBudgets(Long schoolId);

    /** Get budgets filtered by academic year. */
    List<BudgetSummaryDTO> getBudgetsByYear(String academicYear, Long schoolId);

    /** Get budgets filtered by status. */
    List<BudgetSummaryDTO> getBudgetsByStatus(BudgetStatus status, Long schoolId);

    /** Get a single budget with full line item details and variance data. */
    BudgetResponseDTO getBudgetById(Long budgetId, Long schoolId);

    /** Delete a DRAFT budget permanently. */
    void deleteBudget(Long budgetId, Long schoolId);

    /** Get distinct academic years in use — for dropdown. */
    List<String> getAcademicYears(Long schoolId);

    /** Get distinct departments in use — for dropdown. */
    List<String> getDepartments(Long schoolId);
}
