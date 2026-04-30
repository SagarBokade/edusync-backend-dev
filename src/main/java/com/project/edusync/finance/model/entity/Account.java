package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.AccountType;
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
 * Represents a single account in the Chart of Accounts (COA).
 *
 * Supports unlimited hierarchy depth via self-referential parentAccount.
 * Example hierarchy:
 *   1000 — Assets (ASSET)
 *     1100 — Current Assets (ASSET)
 *       1110 — Cash in Hand (ASSET)
 *       1120 — Bank — HDFC (ASSET)
 *     1200 — Fixed Assets (ASSET)
 *   2000 — Liabilities (LIABILITY)
 *   3000 — Equity (EQUITY)
 *   4000 — Income (INCOME)
 *     4100 — Fee Revenue (INCOME)
 *     4200 — Grant Income (INCOME)
 *   5000 — Expenses (EXPENSE)
 *     5100 — Salary Expense (EXPENSE)
 *     5200 — Lab Maintenance (EXPENSE)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "coa_accounts",
       uniqueConstraints = @UniqueConstraint(columnNames = {"code", "school_id"}))
public class Account extends AuditableEntity {

    /**
     * Short alphanumeric code like "1110". Unique per school.
     */
    @Column(name = "code", nullable = false, length = 20)
    private String code;

    /**
     * Human-readable name of the account.
     */
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /**
     * One of: ASSET, LIABILITY, EQUITY, INCOME, EXPENSE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    /**
     * Parent account for hierarchy. Null for root-level accounts (Assets, Liabilities, etc.)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_account_id")
    private Account parentAccount;

    /**
     * Children of this account — populated for display purposes.
     */
    @OneToMany(mappedBy = "parentAccount", fetch = FetchType.LAZY)
    private List<Account> children = new ArrayList<>();

    /**
     * Running balance of this account. Updated whenever a journal line is posted.
     */
    @Column(name = "balance", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * Optional description / usage notes.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether transactions can be posted directly to this account
     * (false for group/header accounts that only aggregate children).
     */
    @Column(name = "is_posting_account", nullable = false)
    @ColumnDefault("true")
    private boolean postingAccount = true;

    /**
     * Whether this account is active. Inactive accounts cannot receive new journal lines.
     */
    @Column(name = "is_active", nullable = false)
    @ColumnDefault("true")
    private boolean active = true;

    /**
     * School scoping — mirrors the pattern used in HRMS entities.
     */
    @Column(name = "school_id")
    private Long schoolId;
}
