package com.project.edusync.finance.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reminder_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(nullable = false)
    private String channel;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "amount_due")
    private BigDecimal amountDue;

    @Column(nullable = false)
    private String status; // "SENT", "FAILED", "QUEUED"

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;
}
