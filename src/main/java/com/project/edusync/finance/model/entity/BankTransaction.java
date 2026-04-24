package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.BankTransactionType;
import com.project.edusync.finance.model.enums.ReconciliationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single transaction from a bank statement.
 *
 * Imported from CSV/Excel or entered manually.
 * The reconciliation engine attempts to auto-match each transaction
 * to a GL journal entry posted on the same date with the same amount.
 *
 * When matched, the bank's book balance and GL balance should converge.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "bank_transactions")
public class BankTransaction extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    /** Cheque / UTR / IMPS / NEFT ref. */
    @Column(name = "instrument_number", length = 100)
    private String instrumentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 10)
    private BankTransactionType transactionType;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    /** Running balance after this transaction as shown on bank statement. */
    @Column(name = "running_balance", precision = 14, scale = 2)
    private BigDecimal runningBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "reconciliation_status", nullable = false, length = 30)
    @ColumnDefault("'UNRECONCILED'")
    private ReconciliationStatus reconciliationStatus = ReconciliationStatus.UNRECONCILED;

    /**
     * The GL journal entry this transaction was matched to.
     * Set during reconciliation.
     */
    @Column(name = "matched_gl_entry_id")
    private Long matchedGlEntryId;

    @Column(name = "reconciliation_notes", length = 500)
    private String reconciliationNotes;

    @Column(name = "school_id")
    private Long schoolId;
}
