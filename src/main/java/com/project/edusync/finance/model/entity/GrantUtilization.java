package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Records a single utilisation of grant money (expense charged to a grant).
 *
 * Examples:
 *   - Research equipment purchase — ₹2,50,000
 *   - Conference travel — ₹45,000
 *   - Manpower (RA salary) — ₹35,000/month
 *
 * Each utilisation can be linked to a GL journal entry for full traceability.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "grant_utilisations")
public class GrantUtilization extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grant_id", nullable = false)
    private Grant grant;

    @Column(name = "utilisation_date", nullable = false)
    private LocalDate utilisationDate;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    /** Expense category: Equipment, Manpower, Travel, Consumables, Overhead. */
    @Column(name = "expense_category", length = 100)
    private String expenseCategory;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    /** Reference to invoice / bill number supporting this expense. */
    @Column(name = "reference_document", length = 100)
    private String referenceDocument;

    /** GL journal entry that records this utilisation. */
    @Column(name = "gl_entry_id")
    private Long glEntryId;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;
}
