package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.DepreciationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DepreciationEntryRepository extends JpaRepository<DepreciationEntry, Long> {
    List<DepreciationEntry> findByAssetIdOrderByDepreciationDateDesc(Long assetId);
    List<DepreciationEntry> findBySchoolIdAndFinancialYearOrderByDepreciationDateDesc(Long schoolId, String financialYear);
    boolean existsByAssetIdAndFinancialYear(Long assetId, String financialYear);

    @Query("SELECT COALESCE(SUM(d.depreciationAmount), 0) FROM DepreciationEntry d WHERE d.schoolId = :schoolId AND d.financialYear = :fy")
    BigDecimal sumDepreciationForYear(@Param("schoolId") Long schoolId, @Param("fy") String fy);
}
