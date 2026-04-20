package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.InstallmentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstallmentAssignmentRepository extends JpaRepository<InstallmentAssignment, Long> {
    List<InstallmentAssignment> findByStudentId(Long studentId);
}
