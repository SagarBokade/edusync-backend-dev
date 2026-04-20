package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.ReminderTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderTemplateRepository extends JpaRepository<ReminderTemplate, Long> {
    List<ReminderTemplate> findByIsActiveTrue();
}
