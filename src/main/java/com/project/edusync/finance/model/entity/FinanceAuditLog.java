package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

/**
 * Immutable audit trail for critical financial events (overrides, deletions, config changes).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "finance_audit_logs")
public class FinanceAuditLog extends AuditableEntity {

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType; // e.g., "MISMATCH_OVERRIDE", "BUDGET_REVISION", "JOURNAL_CANCELLED"

    @Column(name = "entity_name", length = 100)
    private String entityName; // e.g., "VendorBill", "Budget"

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "school_id")
    private Long schoolId;
}
