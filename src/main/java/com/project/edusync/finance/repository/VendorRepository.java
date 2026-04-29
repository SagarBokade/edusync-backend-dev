package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.Vendor;
import com.project.edusync.finance.model.enums.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    List<Vendor> findBySchoolIdOrderByNameAsc(Long schoolId);
    List<Vendor> findBySchoolIdAndStatusOrderByNameAsc(Long schoolId, VendorStatus status);
    Optional<Vendor> findByVendorCodeAndSchoolId(String vendorCode, Long schoolId);
    boolean existsByVendorCodeAndSchoolId(String vendorCode, Long schoolId);
    boolean existsByGstinAndSchoolId(String gstin, Long schoolId);

    @Query("SELECT DISTINCT v.category FROM Vendor v WHERE v.schoolId = :schoolId AND v.category IS NOT NULL ORDER BY v.category ASC")
    List<String> findDistinctCategories(@Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.schoolId = :schoolId AND v.status = :status")
    Long countBySchoolIdAndStatus(@Param("schoolId") Long schoolId, @Param("status") VendorStatus status);

    /** Next sequential vendor code. */
    @Query("SELECT COUNT(v) FROM Vendor v WHERE v.schoolId = :schoolId")
    Long countBySchoolId(@Param("schoolId") Long schoolId);
}
