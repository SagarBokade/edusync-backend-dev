package com.project.edusync.finance.dto.grant;

import com.project.edusync.finance.model.enums.GrantStatus;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GrantRequestDTO(
    @NotBlank String grantingAgency,
    @NotBlank String grantTitle,
    String grantReference,
    String principalInvestigator,
    String department,
    @NotNull @DecimalMin("0.01") BigDecimal sanctionedAmount,
    BigDecimal receivedAmount,
    LocalDate startDate,
    LocalDate endDate,
    GrantStatus status,
    Long incomeAccountId,
    LocalDate complianceDueDate,
    String objectives,
    String notes
) {}
