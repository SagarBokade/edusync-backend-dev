package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.ScholarshipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScholarshipTypeRepository extends JpaRepository<ScholarshipType, Long> {
}
