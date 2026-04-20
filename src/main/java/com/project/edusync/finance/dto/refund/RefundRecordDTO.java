package com.project.edusync.finance.dto.refund;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RefundRecordDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long paymentId;
    private String invoiceNumber;
    private BigDecimal refundAmount;
    private String reason;
    private String refundMethod;
    private String status;
    private String remarks;
    private String requestedAt;
    private String processedAt;
}
