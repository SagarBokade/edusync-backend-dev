package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

/**
 * Represents a bank account used by the institution.
 *
 * Bank transactions are imported (or manually entered) against this account
 * for reconciliation with GL entries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bank_accounts")
public class BankAccount extends AuditableEntity {

    @Column(name = "account_name", nullable = false, length = 200)
    private String accountName;

    @Column(name = "account_number", nullable = false, length = 50)
    private String accountNumber;

    @Column(name = "bank_name", nullable = false, length = 150)
    private String bankName;

    @Column(name = "branch_name", length = 150)
    private String branchName;

    @Column(name = "ifsc_code", length = 15)
    private String ifscCode;

    @Column(name = "account_type", length = 50)
    private String accountType; // Current, Savings, OD, etc.

    /**
     * Linked COA account (e.g., "1120 — State Bank Current Account").
     * GL entries should be cross-referenced against this account for reconciliation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coa_account_id")
    private Account coaAccount;

    /** Current balance as per bank statement (updated after each import). */
    @Column(name = "statement_balance", precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal statementBalance = BigDecimal.ZERO;

    /** Current balance as per GL (computed from COA account). */
    @Column(name = "book_balance", precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal bookBalance = BigDecimal.ZERO;

    @Column(name = "is_active")
    @ColumnDefault("true")
    private Boolean isActive = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;
}
