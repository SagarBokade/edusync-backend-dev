package com.project.edusync.finance.dto.payment;

import com.project.edusync.finance.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Used for POST /payments/record-offline [cite: 15]
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordOfflinePaymentDTO {
    @NotNull
    private Long invoiceId;
    @NotNull
    private Long studentId;
    @NotNull
    private BigDecimal amountPaid;
    @NotNull
    private PaymentMethod paymentMethod; // e.g., "CASH" or "CHECK"
    private String transactionId; // e.g., Check number
    private LocalDateTime paymentDate;
    private String notes;
    // Getters and Setters
}