package com.project.edusync.finance.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used for POST /payments/initiate (Parent API) [cite: 21]
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequestDTO {
    @NotNull
    private Long invoiceId;
    // might also include amount if partial payments are allowed
    // private BigDecimal amount;
    // Getters and Setters
}