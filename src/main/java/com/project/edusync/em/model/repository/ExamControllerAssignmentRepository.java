package com.project.edusync.em.model.repository;

import com.project.edusync.em.model.entity.ExamControllerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamControllerAssignmentRepository extends JpaRepository<ExamControllerAssignment, Long> {

    Optional<ExamControllerAssignment> findByExamIdAndActiveTrue(Long examId);

    boolean existsByExamIdAndStaffIdAndActiveTrue(Long examId, Long staffId);
}

