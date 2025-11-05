package com.project.edusync.uis.repository.details;

import com.project.edusync.adm.model.entity.Room;
import com.project.edusync.uis.model.entity.details.TeacherDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TeacherDetailsRepository extends JpaRepository<TeacherDetails, Integer> {

    @Query("SELECT t FROM TeacherDetails t WHERE t.id = :teacherId")
    Optional<TeacherDetails> findActiveById(Long teacherId);
}
