package com.project.edusync.finance.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

/**
 * A single category line within a departmental Budget.
 *
 * Examples:
 *   - Category: "Faculty Salary"   → Allocated: ₹1,20,00,000 | Actual: ₹1,18,40,000
 *   - Category: "Lab Equipment"    → Allocated: ₹25,00,000   | Actual: ₹18,20,000
 *   - Category: "Research Grants"  → Allocated: ₹50,00,000   | Actual: ₹50,00,000
 *
 * The 'linkedAccount' FK to Account allows GL-driven auto-calculation of actualAmount.
 * If linkedAccount is set, actualAmount is updated whenever a GL entry posts to that account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "finance_budget_line_items")
public class BudgetLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_item_id")
    private Long lineItemId;

    /**
     * Parent budget.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    /**
     * Human-readable label for this category (e.g., "Faculty Salary", "Lab Supplies").
     */
    @Column(name = "category", nullable = false, length = 150)
    private String category;

    /**
     * Optional link to a COA account.
     * When set, actual spending is automatically tracked from GL entries posted to this account.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_account_id")
    private Account linkedAccount;

    /**
     * The budgeted (planned) amount for this category.
     */
    @Column(name = "allocated_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal allocatedAmount = BigDecimal.ZERO;

    /**
     * The actual amount spent so far (updated from GL or manually).
     */
    @Column(name = "actual_amount", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal actualAmount = BigDecimal.ZERO;

    /**
     * Computed variance: allocatedAmount - actualAmount.
     * Positive = under budget (good), Negative = over budget (bad).
     */
    @Column(name = "variance", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal variance = BigDecimal.ZERO;

    /**
     * Additional notes for this line (e.g., "Includes 2 new faculty hires").
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Line sort order for display.
     */
    @Column(name = "line_number")
    private Integer lineNumber;

    /**
     * Recalcates variance field from allocated vs actual.
     */
    public void recalcVariance() {
        this.variance = this.allocatedAmount.subtract(this.actualAmount);
    }
}
