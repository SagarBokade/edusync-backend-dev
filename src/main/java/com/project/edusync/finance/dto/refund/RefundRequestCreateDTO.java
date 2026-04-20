package com.project.edusync.finance.dto.refund;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundRequestCreateDTO {
    @NotNull
    private Long studentId;
    @NotBlank
    private String studentName;
    private Long paymentId;
    private String invoiceNumber;
    @NotNull
    private BigDecimal refundAmount;
    @NotBlank
    private String reason;
    @NotBlank
    private String refundMethod;
}
