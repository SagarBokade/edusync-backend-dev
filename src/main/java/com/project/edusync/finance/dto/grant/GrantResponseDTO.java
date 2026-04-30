package com.project.edusync.finance.dto.grant;

import com.project.edusync.finance.model.enums.GrantStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GrantResponseDTO(
    Long id, UUID uuid,
    String grantingAgency, String grantTitle, String grantReference,
    String principalInvestigator, String department,
    BigDecimal sanctionedAmount, BigDecimal receivedAmount,
    BigDecimal utilisedAmount, BigDecimal availableBalance,
    double utilisationPct,
    LocalDate startDate, LocalDate endDate,
    GrantStatus status,
    Long incomeAccountId, String incomeAccountCode, String incomeAccountName,
    LocalDate complianceDueDate,
    boolean nearingExpiry, boolean complianceOverdue,
    String objectives, String notes,
    List<GrantUtilizationResponseDTO> utilisations,
    LocalDateTime createdAt, String createdBy
) {}
