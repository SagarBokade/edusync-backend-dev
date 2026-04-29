package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.Grant;
import com.project.edusync.finance.model.enums.GrantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GrantRepository extends JpaRepository<Grant, Long> {
    List<Grant> findBySchoolIdOrderByStartDateDesc(Long schoolId);
    List<Grant> findBySchoolIdAndStatusOrderByStartDateDesc(Long schoolId, GrantStatus status);
    List<Grant> findBySchoolIdAndDepartmentOrderByStartDateDesc(Long schoolId, String department);

    @Query("SELECT COALESCE(SUM(g.sanctionedAmount), 0) FROM Grant g WHERE g.schoolId = :schoolId AND g.status IN ('ACTIVE','PARTIALLY_UTILISED','FULLY_UTILISED')")
    BigDecimal sumActiveSanctionedAmount(@Param("schoolId") Long schoolId);

    @Query("SELECT COALESCE(SUM(g.utilisedAmount), 0) FROM Grant g WHERE g.schoolId = :schoolId")
    BigDecimal sumTotalUtilised(@Param("schoolId") Long schoolId);

    /** Grants nearing expiry (within next 90 days). */
    @Query("SELECT g FROM Grant g WHERE g.schoolId = :schoolId AND g.endDate BETWEEN :today AND :threshold AND g.status IN ('ACTIVE','PARTIALLY_UTILISED')")
    List<Grant> findGrantsNearingExpiry(@Param("schoolId") Long schoolId, @Param("today") LocalDate today, @Param("threshold") LocalDate threshold);
}
