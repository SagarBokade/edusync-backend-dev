package com.project.edusync.finance.dto.configuration;

import com.project.edusync.finance.model.enums.GatewayEnvironment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used for GET /payment-gateways
// Based on finance.payment_gateways
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentGatewayResponseDTO {
    private Long gatewayId;
    private String providerName;
    private boolean isActive;
    private GatewayEnvironment environment; // 'SANDBOX', 'PRODUCTION'

    // CRITICAL: DO NOT expose api_key or api_secret in a response DTO.
    // Getters and Setters
}