package com.project.edusync.finance.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * A single debit or credit line within a Journal Entry.
 *
 * Rules:
 * - Exactly one of debitAmount or creditAmount must be non-zero per line.
 * - The sum of ALL debitAmounts in a JournalEntry must equal the sum of ALL creditAmounts.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gl_journal_lines")
public class JournalLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_id")
    private Long lineId;

    /**
     * Parent journal entry this line belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    private JournalEntry journalEntry;

    /**
     * The Chart of Accounts account being debited or credited.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * Debit amount. Must be 0.00 if this is a credit line.
     */
    @Column(name = "debit_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal debitAmount = BigDecimal.ZERO;

    /**
     * Credit amount. Must be 0.00 if this is a debit line.
     */
    @Column(name = "credit_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal creditAmount = BigDecimal.ZERO;

    /**
     * Line-level narration for additional context (e.g., "Fee for Student STU-001 - Sem 2").
     */
    @Column(name = "narration", length = 500)
    private String narration;

    /**
     * Sort order for display within the entry.
     */
    @Column(name = "line_number")
    private Integer lineNumber;
}
