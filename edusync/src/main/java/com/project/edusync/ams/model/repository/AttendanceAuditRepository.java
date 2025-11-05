package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.AttendanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceAuditRepository extends JpaRepository<AttendanceAudit, Long> {

    /**
     * Finds the complete modification history for a specific daily attendance record.
     */
    List<AttendanceAudit> findByDailyAttendanceId(Long dailyAttendanceId);

}