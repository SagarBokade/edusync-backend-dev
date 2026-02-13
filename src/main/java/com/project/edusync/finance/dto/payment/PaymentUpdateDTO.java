package com.project.edusync.finance.dto.payment;

import com.project.edusync.finance.model.enums.PaymentMethod;
import com.project.edusync.finance.model.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

// Used for PUT /payments/{paymentId}
@Data
@NoArgsConstructor
public class PaymentUpdateDTO {

    // Admin might need to correct the transaction ID (e.g., check number)
    private String transactionId;

    // Admin might change the method if it was recorded incorrectly
    @NotNull
    private PaymentMethod paymentMethod;

    // Admin might need to void a payment (e.g., FAILED)
    @NotNull
    private PaymentStatus status;

    private String notes;
}
