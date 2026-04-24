package com.project.edusync.finance.dto.audit;

import java.time.LocalDateTime;

public record FinanceAuditLogResponseDTO(
    Long id,
    String actionType,
    String entityName,
    Long entityId,
    String performedBy,
    LocalDateTime actionTimestamp,
    String description,
    String ipAddress
) {}
