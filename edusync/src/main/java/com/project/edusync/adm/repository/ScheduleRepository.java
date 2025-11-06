package com.project.edusync.adm.repository;

import com.project.edusync.adm.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.uuid = :scheduleId AND s.isActive = true")
    Optional<Schedule> findActiveById(UUID scheduleId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Schedule s WHERE s.uuid = :scheduleId AND s.isActive = true")
    boolean existsActiveById(UUID scheduleId);

    @Transactional
    @Modifying
    @Query("UPDATE Schedule s SET s.isActive = false WHERE s.uuid = :scheduleId")
    void softDeleteById(UUID scheduleId);

    @Query("SELECT s FROM Schedule s WHERE s.section.uuid = :sectionId AND s.isActive = true")
    List<Schedule> findAllActiveBySectionUuid(UUID sectionId);

    // --- Conflict Detection Queries ---

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Schedule s " +
            "WHERE s.teacher.id = :teacherId " +
            "AND s.timeslot.uuid = :timeslotId " +
            "AND s.isActive = true " +
            "AND (:scheduleId IS NULL OR s.uuid != :scheduleId)")
    boolean existsTeacherConflict(Long teacherId, UUID timeslotId, UUID scheduleId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Schedule s " +
            "WHERE s.room.uuid = :roomId " +
            "AND s.timeslot.uuid = :timeslotId " +
            "AND s.isActive = true " +
            "AND (:scheduleId IS NULL OR s.uuid != :scheduleId)")
    boolean existsRoomConflict(UUID roomId, UUID timeslotId, UUID scheduleId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Schedule s " +
            "WHERE s.section.uuid = :sectionId " +
            "AND s.timeslot.uuid = :timeslotId " +
            "AND s.isActive = true " +
            "AND (:scheduleId IS NULL OR s.uuid != :scheduleId)")
    boolean existsSectionConflict(UUID sectionId, UUID timeslotId, UUID scheduleId);
}