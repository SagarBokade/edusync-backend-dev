package com.project.edusync.finance.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "refund_amount", nullable = false)
    private BigDecimal refundAmount;

    @Column(nullable = false)
    private String reason;

    @Column(name = "refund_method", nullable = false)
    private String refundMethod;

    @Column(nullable = false)
    private String status; // "REQUESTED", "APPROVED", "REJECTED", "PROCESSED"

    private String remarks;

    @CreationTimestamp
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
