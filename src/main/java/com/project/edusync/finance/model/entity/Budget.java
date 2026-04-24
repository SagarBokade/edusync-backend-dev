package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.BudgetStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an annual departmental budget.
 *
 * Each Budget belongs to one department for one academic year.
 * It is broken down into BudgetLineItems (one per expense/income category).
 *
 * Approval flow: DRAFT → SUBMITTED → APPROVED (by Finance Admin)
 * Once APPROVED, actual spending from the GL is compared against
 * each line item to produce the "Budget vs Actual" variance report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "finance_budgets",
       uniqueConstraints = @UniqueConstraint(columnNames = {"department_name", "academic_year", "school_id"}))
public class Budget extends AuditableEntity {

    /**
     * The department this budget belongs to (e.g., "CSE", "Admin", "Library").
     * Stored as a free-text string — no FK to a department entity to keep it flexible.
     */
    @Column(name = "department_name", nullable = false, length = 150)
    private String departmentName;

    /**
     * Academic year in format "2025-2026".
     */
    @Column(name = "academic_year", nullable = false, length = 10)
    private String academicYear;

    /**
     * Optional title for this budget (e.g., "CSE Dept FY 2025-26 Annual Budget").
     */
    @Column(name = "title", length = 200)
    private String title;

    /**
     * Sum of all line item allocatedAmounts. Computed and stored for fast querying.
     */
    @Column(name = "total_allocated", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal totalAllocated = BigDecimal.ZERO;

    /**
     * Sum of actual spending against this budget (pulled from GL entries for matching accounts).
     * Updated whenever a relevant GL entry is posted.
     */
    @Column(name = "total_spent", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'DRAFT'")
    private BudgetStatus status = BudgetStatus.DRAFT;

    /**
     * Username of the Finance Admin who approved/rejected this budget.
     */
    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    /**
     * Notes from the approver (reason for rejection or revision request).
     */
    @Column(name = "reviewer_notes", columnDefinition = "TEXT")
    private String reviewerNotes;

    /**
     * Notes from the submitter.
     */
    @Column(name = "submitter_notes", columnDefinition = "TEXT")
    private String submitterNotes;

    /**
     * School scoping.
     */
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * The individual expense/income categories within this budget.
     */
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BudgetLineItem> lineItems = new ArrayList<>();

    /**
     * Convenience method to add a line item and maintain the bi-directional relationship.
     */
    public void addLineItem(BudgetLineItem item) {
        lineItems.add(item);
        item.setBudget(this);
        this.totalAllocated = this.totalAllocated.add(item.getAllocatedAmount());
    }

    /**
     * Recalculates totalAllocated from all line items.
     */
    public void recalculateTotals() {
        this.totalAllocated = lineItems.stream()
                .map(BudgetLineItem::getAllocatedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalSpent = lineItems.stream()
                .map(BudgetLineItem::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
