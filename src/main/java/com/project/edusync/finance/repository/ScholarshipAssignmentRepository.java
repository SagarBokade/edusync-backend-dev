package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.ScholarshipAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipAssignmentRepository extends JpaRepository<ScholarshipAssignment, Long> {
    List<ScholarshipAssignment> findByStudentId(Long studentId);
}
