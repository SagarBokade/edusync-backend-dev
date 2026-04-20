package com.project.edusync.finance.dto.reminder;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReminderLogDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String templateName;
    private String channel;
    private String invoiceNumber;
    private BigDecimal amountDue;
    private String status;
    private String sentAt;
}
