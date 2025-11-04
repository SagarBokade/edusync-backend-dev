package com.project.edusync.finance.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used for POST /payments/verify (Parent API) [cite: 21]
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequestDTO {
    @NotNull
    private String gatewayTransactionId;
    @NotNull
    private String orderId;
    // Other gateway-specific fields like 'signature'
    // Getters and Setters
}
