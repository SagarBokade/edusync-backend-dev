package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.GrantUtilization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GrantUtilizationRepository extends JpaRepository<GrantUtilization, Long> {
    List<GrantUtilization> findByGrantIdOrderByUtilisationDateDesc(Long grantId);
    List<GrantUtilization> findByGrantIdAndExpenseCategoryOrderByUtilisationDateDesc(Long grantId, String expenseCategory);

    @Query("SELECT COALESCE(SUM(u.amount), 0) FROM GrantUtilization u WHERE u.grant.id = :grantId")
    BigDecimal sumUtilisedForGrant(@Param("grantId") Long grantId);

    @Query("SELECT DISTINCT u.expenseCategory FROM GrantUtilization u WHERE u.grant.id = :grantId AND u.expenseCategory IS NOT NULL")
    List<String> findDistinctCategories(@Param("grantId") Long grantId);
}
