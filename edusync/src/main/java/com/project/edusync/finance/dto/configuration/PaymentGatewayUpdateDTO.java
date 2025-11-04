package com.project.edusync.finance.dto.configuration;

import com.project.edusync.finance.model.enums.GatewayEnvironment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used for PUT /payment-gateways/{gatewayId}
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayUpdateDTO {
    private String apiKey; // Sent TO the server, not FROM
    private String apiSecret; // Sent TO the server, not FROM
    @NotNull
    private boolean isActive;
    @NotNull
    private GatewayEnvironment environment;
    // Getters and Setters
}