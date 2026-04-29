package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.FixedAsset;
import com.project.edusync.finance.model.enums.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FixedAssetRepository extends JpaRepository<FixedAsset, Long> {
    List<FixedAsset> findBySchoolIdOrderByPurchaseDateDesc(Long schoolId);
    List<FixedAsset> findBySchoolIdAndStatusOrderByNameAsc(Long schoolId, AssetStatus status);
    List<FixedAsset> findBySchoolIdAndAssetCategoryOrderByNameAsc(Long schoolId, String assetCategory);
    Optional<FixedAsset> findByAssetCodeAndSchoolId(String assetCode, Long schoolId);

    @Query("SELECT DISTINCT fa.assetCategory FROM FixedAsset fa WHERE fa.schoolId = :schoolId AND fa.assetCategory IS NOT NULL ORDER BY fa.assetCategory")
    List<String> findDistinctCategories(@Param("schoolId") Long schoolId);

    @Query("SELECT COALESCE(SUM(fa.purchaseCost), 0) FROM FixedAsset fa WHERE fa.schoolId = :schoolId AND fa.status != 'DISPOSED' AND fa.status != 'WRITTEN_OFF'")
    BigDecimal sumGrossCost(@Param("schoolId") Long schoolId);

    @Query("SELECT COALESCE(SUM(fa.currentBookValue), 0) FROM FixedAsset fa WHERE fa.schoolId = :schoolId AND fa.status = 'ACTIVE'")
    BigDecimal sumBookValue(@Param("schoolId") Long schoolId);

    @Query("SELECT COALESCE(SUM(fa.accumulatedDepreciation), 0) FROM FixedAsset fa WHERE fa.schoolId = :schoolId")
    BigDecimal sumAccumulatedDepreciation(@Param("schoolId") Long schoolId);

    /** Assets due for depreciation in this FY (active, not fully depreciated). */
    @Query("SELECT fa FROM FixedAsset fa WHERE fa.schoolId = :schoolId AND fa.status = 'ACTIVE' AND fa.currentBookValue > fa.salvageValue")
    List<FixedAsset> findAssetsEligibleForDepreciation(@Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(fa) FROM FixedAsset fa WHERE fa.schoolId = :schoolId AND fa.assetCode LIKE :prefix%")
    Long countByPrefix(@Param("schoolId") Long schoolId, @Param("prefix") String prefix);
}
