package com.project.edusync.finance.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response from POST /payments/initiate [cite: 21]
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentResponseDTO {
    private String clientSecret; // e.g., for Stripe
    private String paymentGatewayUrl; // e.g., for redirect
    private String orderId; // Internal or gateway order ID
    // Getters and Setters
}