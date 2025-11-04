package com.project.edusync.adm.repository;

import com.project.edusync.adm.model.entity.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotRepository extends JpaRepository<Timeslot, Long> {
}
