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
@Table(name = "scholarship_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScholarshipType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "discount_type", nullable = false)
    private String discountType; // "PERCENTAGE" or "FIXED"

    @Column(name = "discount_value", nullable = false)
    private BigDecimal discountValue;

    @Column(name = "eligibility_criteria")
    private String eligibilityCriteria;

    @Column(name = "max_recipients")
    private Integer maxRecipients;

    @Column(name = "active_count")
    @Builder.Default
    private Integer activeCount = 0;

    @Column(name = "total_discount_issued")
    @Builder.Default
    private BigDecimal totalDiscountIssued = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
