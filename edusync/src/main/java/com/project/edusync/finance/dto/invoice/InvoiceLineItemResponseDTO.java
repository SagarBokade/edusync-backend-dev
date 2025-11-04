package com.project.edusync.finance.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// Based on finance.invoice_line_items
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceLineItemResponseDTO {
    private Long lineItemId;
    private String description;
    private BigDecimal amount;
    // Getters and Setters
}