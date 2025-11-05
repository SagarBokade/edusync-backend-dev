package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.StaffDailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffDailyAttendanceRepository extends JpaRepository<StaffDailyAttendance, Long> {

    /**
     * Finds a record by the composite UNIQUE KEY (staff_id, attendance_date).
     * Essential for daily time tracking.
     */
    Optional<StaffDailyAttendance> findByStaffIdAndAttendanceDate(Long staffId, LocalDate attendanceDate);

    /**
     * Finds all records for a staff member within a date range.
     */
    List<StaffDailyAttendance> findByStaffId(Long staffId);
}