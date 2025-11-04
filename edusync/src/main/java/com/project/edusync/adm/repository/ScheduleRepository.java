package com.project.edusync.adm.repository;

import com.project.edusync.adm.model.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
