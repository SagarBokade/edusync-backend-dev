package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.Budget;
import com.project.edusync.finance.model.enums.BudgetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /** All budgets for a school, newest first. */
    List<Budget> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);

    /** Budgets for a school filtered by academic year. */
    List<Budget> findBySchoolIdAndAcademicYearOrderByDepartmentNameAsc(Long schoolId, String academicYear);

    /** Budgets for a specific department across all years. */
    List<Budget> findBySchoolIdAndDepartmentNameOrderByAcademicYearDesc(Long schoolId, String departmentName);

    /** Budgets by status (e.g., all SUBMITTED budgets awaiting approval). */
    List<Budget> findBySchoolIdAndStatusOrderByCreatedAtDesc(Long schoolId, BudgetStatus status);

    /** Check uniqueness: only one budget per dept/year/school. */
    boolean existsByDepartmentNameAndAcademicYearAndSchoolId(String departmentName, String academicYear, Long schoolId);

    Optional<Budget> findByDepartmentNameAndAcademicYearAndSchoolId(String departmentName, String academicYear, Long schoolId);

    /** Distinct academic years in use — for the year filter dropdown. */
    @Query("SELECT DISTINCT b.academicYear FROM Budget b WHERE b.schoolId = :schoolId ORDER BY b.academicYear DESC")
    List<String> findDistinctAcademicYears(@Param("schoolId") Long schoolId);

    /** Distinct departments in use — for the department filter dropdown. */
    @Query("SELECT DISTINCT b.departmentName FROM Budget b WHERE b.schoolId = :schoolId ORDER BY b.departmentName ASC")
    List<String> findDistinctDepartments(@Param("schoolId") Long schoolId);

    /** Summary query: count of budgets per status for the dashboard overview. */
    @Query("SELECT b.status, COUNT(b) FROM Budget b WHERE b.schoolId = :schoolId GROUP BY b.status")
    List<Object[]> countByStatus(@Param("schoolId") Long schoolId);
}
