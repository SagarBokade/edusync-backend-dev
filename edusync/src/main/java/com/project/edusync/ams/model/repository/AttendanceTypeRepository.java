package com.project.edusync.ams.model.repository;

import com.project.edusync.ams.model.entity.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceTypeRepository extends JpaRepository<AttendanceType, Long> {

    /**
     * Finds an attendance type by its unique short code (e.g., 'P', 'A', 'UA').
     * Essential for system logic and quick data retrieval.
     */
    Optional<AttendanceType> findByShortCode(String shortCode);

    /**
     * Finds an attendance type by its unique short code (e.g., 'P', 'A', 'UA').
     * Case-insensitive lookup is convenient for API clients that may send 'p' or 'P'.
     */
    Optional<AttendanceType> findByShortCodeIgnoreCase(String shortCode);

    /**
     * Finds an attendance type by its unique full name (e.g., 'Present', 'Excused Absence').
     */
    Optional<AttendanceType> findByTypeName(String typeName);

    /**
     * Retrieves all attendance types that mark a student/staff as present.
     */
    List<AttendanceType> findByIsPresentMarkTrue();

    /**
     * Retrieves all attendance types that mark a student/staff as absent (for reporting/metrics).
     */
    List<AttendanceType> findByIsAbsenceMarkTrue();

    /**
     * Retrieves all attendance types that mark a student/staff as late.
     */
    List<AttendanceType> findByIsLateMarkTrue();

    /**
     * Finds a single active attendance type by its primary key.
     * @param id The internal ID of the Attendance Type.
     * @return Optional containing the active Attendance Type.
     */
    Optional<AttendanceType> findByIdAndIsActiveTrue(Long id);

    /**
     * Finds an active attendance type by its unique short code (e.g., 'P', 'A').
     */
    Optional<AttendanceType> findByShortCodeAndIsActiveTrue(String shortCode);

    /**
     * Finds an active attendance type by its unique full name.
     */
    Optional<AttendanceType> findByTypeNameAndIsActiveTrue(String typeName);

    /**
     * Retrieves all active attendance types for use in UI/logic.
     */
    List<AttendanceType> findByIsActiveTrue();

    // The methods below are needed for the soft-delete check in the Service layer:

    /**
     * Checks if any StudentDailyAttendance record uses this type ID.
     * NOTE: This assumes a count method exists in the StudentDailyAttendanceRepository.
     * We simulate the check here. In a service, this would be a check against the child table.
     */
    // @Query("SELECT COUNT(s) FROM StudentDailyAttendance s WHERE s.attendanceType.id = :typeId")
    // Long countStudentUsage(@Param("typeId") Long typeId);
}