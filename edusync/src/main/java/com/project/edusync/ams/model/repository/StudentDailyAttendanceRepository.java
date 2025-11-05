package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.StudentDailyAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentDailyAttendanceRepository extends JpaRepository<StudentDailyAttendance, Long> {

    /**
     * Finds a record by the composite UNIQUE KEY (student_id, attendance_date).
     * Essential for preventing duplicate daily entries.
     */
    Optional<StudentDailyAttendance> findByStudentIdAndAttendanceDate(Long studentId, LocalDate attendanceDate);

    /**
     * Finds all records for a single student within a date range (for reports/history).
     */
    List<StudentDailyAttendance> findByStudentIdAndAttendanceDateBetween(
            Long studentId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Finds all students marked as Absent (where the AttendanceType has is_absence_mark = TRUE)
     * on a specific date (useful for Truancy/Absence List reports).
     */
    @Query("SELECT sda FROM StudentDailyAttendance sda JOIN sda.attendanceType at WHERE at.isAbsenceMark = true AND sda.attendanceDate = :date")
    List<StudentDailyAttendance> findAbsentStudentsByDate(@Param("date") LocalDate date);
}