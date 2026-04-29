package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.JournalEntryStatus;
import com.project.edusync.finance.model.enums.JournalReferenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A Journal Entry is the atomic unit of the General Ledger.
 *
 * Every financial transaction in the system (student payment, salary disbursement,
 * vendor bill payment, etc.) must produce a Journal Entry where:
 *
 *   SUM(debitAmounts of all lines) == SUM(creditAmounts of all lines)
 *
 * This enforces the fundamental double-entry accounting equation:
 *   Assets = Liabilities + Equity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "gl_journal_entries")
public class JournalEntry extends AuditableEntity {

    /**
     * Auto-generated human-readable reference like "JE-2025-001234".
     */
    @Column(name = "entry_number", nullable = false, unique = true, length = 30)
    private String entryNumber;

    /**
     * The accounting date (not necessarily the system timestamp).
     * Determines which accounting period this entry belongs to.
     */
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    /**
     * Human-readable description of the transaction for the ledger.
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    /**
     * The source module that generated this entry.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", nullable = false, length = 30)
    private JournalReferenceType referenceType;

    /**
     * The primary key of the originating record (e.g., Payment.paymentId).
     * Null for MANUAL entries.
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * Lifecycle status of this entry.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @ColumnDefault("'DRAFT'")
    private JournalEntryStatus status = JournalEntryStatus.DRAFT;

    /**
     * If this entry was created by reversing another entry, stores the original entry's id.
     */
    @Column(name = "reversal_of_entry_id")
    private Long reversalOfEntryId;

    /**
     * The username of the person who posted (finalised) this entry.
     */
    @Column(name = "posted_by", length = 100)
    private String postedBy;

    /**
     * School scoping.
     */
    @Column(name = "school_id")
    private Long schoolId;

    /**
     * The individual debit/credit lines. A valid entry must have >= 2 lines.
     */
    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JournalLine> lines = new ArrayList<>();

    /**
     * Convenience method to add a line, maintaining the bi-directional relationship.
     */
    public void addLine(JournalLine line) {
        lines.add(line);
        line.setJournalEntry(this);
    }
}
