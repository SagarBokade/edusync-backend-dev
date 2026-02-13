package com.project.edusync.uis.repository;

import com.project.edusync.uis.model.entity.Student;
import com.project.edusync.uis.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student,Long> {
    boolean existsByEnrollmentNumber(String enrollmentNumber);

    Optional<Student> findByUserProfile(UserProfile profile);
}
