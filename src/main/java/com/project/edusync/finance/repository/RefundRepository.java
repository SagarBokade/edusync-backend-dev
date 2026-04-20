package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.RefundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundRepository extends JpaRepository<RefundRecord, Long> {
    List<RefundRecord> findByStudentId(Long studentId);
}
