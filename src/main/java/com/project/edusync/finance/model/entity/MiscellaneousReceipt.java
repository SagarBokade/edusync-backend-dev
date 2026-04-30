package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Records miscellaneous income/receipts outside of standard student fees.
 * Examples: Hall rental, transcript fees, fine collection, scrap sale.
 * 
 * Generates a GL entry: Dr Bank/Cash (1xxx), Cr Income/Miscellaneous (4xxx).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "misc_receipts")
public class MiscellaneousReceipt extends AuditableEntity {

    /** Auto-generated receipt number: "REC-2025-001" */
    @Column(name = "receipt_number", nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    /** Payer name (person or organization) */
    @Column(name = "received_from", nullable = false, length = 150)
    private String receivedFrom;

    @Column(name = "description", nullable = false, length = 300)
    private String description;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    /** Payment mode: Cash, Cheque, UPI, Bank Transfer */
    @Column(name = "payment_mode", nullable = false, length = 50)
    private String paymentMode;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber; // Cheque number / UTR

    /** The revenue account this receipt belongs to (e.g. 4300 - Hall Rental) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_account_id", nullable = false)
    private Account incomeAccount;

    /** The bank or cash account to which money was received */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_account_id", nullable = false)
    private Account depositAccount;

    @Column(name = "gl_entry_id")
    private Long glEntryId;

    @Column(name = "school_id")
    private Long schoolId;
}
