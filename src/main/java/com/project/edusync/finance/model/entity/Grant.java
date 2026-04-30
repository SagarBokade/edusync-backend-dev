package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.GrantStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A research grant, government scheme, or external funding received by the institution.
 *
 * Examples:
 *   - AICTE Research Grant (₹25L for 3 years)
 *   - DBT Biotechnology Grant
 *   - CSR Funding from TCS
 *   - SPARC / CRS International collaboration
 *
 * Grant utilisation is tracked via GrantUtilization records.
 * Utilisation can be linked to GL entries for automatic tracking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "grants")
public class Grant extends AuditableEntity {

    /** Granting agency/body: "AICTE", "DBT", "DST", "CSIR", "TCS CSR", etc. */
    @Column(name = "granting_agency", nullable = false, length = 200)
    private String grantingAgency;

    @Column(name = "grant_title", nullable = false, length = 300)
    private String grantTitle;

    /** Internal reference number / scheme code. */
    @Column(name = "grant_reference", length = 100)
    private String grantReference;

    /** PI name (Principal Investigator). */
    @Column(name = "principal_investigator", length = 150)
    private String principalInvestigator;

    @Column(name = "department", length = 150)
    private String department;

    /** Total sanctioned grant amount. */
    @Column(name = "sanctioned_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal sanctionedAmount;

    /** Amount actually received so far (may come in tranches). */
    @Column(name = "received_amount", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal receivedAmount = BigDecimal.ZERO;

    /** Total amount utilised so far across all utilisation records. */
    @Column(name = "utilised_amount", nullable = false, precision = 14, scale = 2)
    @ColumnDefault("0.00")
    private BigDecimal utilisedAmount = BigDecimal.ZERO;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'APPLIED'")
    private GrantStatus status = GrantStatus.APPLIED;

    /**
     * COA account into which grant funds are credited.
     * Typically an Income account (e.g., "4200 — Grant Income").
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "income_account_id")
    private Account incomeAccount;

    @Column(name = "compliance_due_date")
    private LocalDate complianceDueDate;

    @Column(name = "objectives", columnDefinition = "TEXT")
    private String objectives;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;

    @OneToMany(mappedBy = "grant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<GrantUtilization> utilisations = new ArrayList<>();

    /** Available = received - utilised. */
    public BigDecimal getAvailableBalance() {
        return receivedAmount.subtract(utilisedAmount);
    }
}
