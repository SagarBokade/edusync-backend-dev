package com.project.edusync.adm.repository;

import com.project.edusync.adm.model.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
