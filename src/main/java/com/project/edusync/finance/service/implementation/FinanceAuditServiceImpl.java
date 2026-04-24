package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.audit.FinanceAuditLogResponseDTO;
import com.project.edusync.finance.model.entity.FinanceAuditLog;
import com.project.edusync.finance.repository.FinanceAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FinanceAuditServiceImpl {

    private final FinanceAuditLogRepository auditLogRepository;

    public void logAction(String actionType, String entityName, Long entityId, String description, Long schoolId) {
        FinanceAuditLog auditLog = new FinanceAuditLog();
        auditLog.setActionType(actionType);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDescription(description);
        auditLog.setSchoolId(schoolId);
        auditLog.setPerformedBy(getCurrentUsername());
        auditLog.setActionTimestamp(LocalDateTime.now());
        // Note: For IP address we could extract it from web context, leaving it blank or system for now.
        auditLog.setIpAddress("SYSTEM");
        
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<FinanceAuditLogResponseDTO> getAuditLogs(Long schoolId) {
        return auditLogRepository.findBySchoolIdOrderByActionTimestampDesc(schoolId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FinanceAuditLogResponseDTO> getAuditLogsForEntity(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityIdOrderByActionTimestampDesc(entityName, entityId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system_user";
        }
    }

    private FinanceAuditLogResponseDTO toDTO(FinanceAuditLog log) {
        return new FinanceAuditLogResponseDTO(
            log.getId(), log.getActionType(), log.getEntityName(), log.getEntityId(),
            log.getPerformedBy(), log.getActionTimestamp(), log.getDescription(), log.getIpAddress()
        );
    }
}
