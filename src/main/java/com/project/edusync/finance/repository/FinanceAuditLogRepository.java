package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.FinanceAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinanceAuditLogRepository extends JpaRepository<FinanceAuditLog, Long> {
    
    List<FinanceAuditLog> findBySchoolIdOrderByActionTimestampDesc(Long schoolId);
    
    List<FinanceAuditLog> findByEntityNameAndEntityIdOrderByActionTimestampDesc(String entityName, Long entityId);
}
