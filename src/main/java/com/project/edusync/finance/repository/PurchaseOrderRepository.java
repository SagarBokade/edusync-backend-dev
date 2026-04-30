package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.PurchaseOrder;
import com.project.edusync.finance.model.enums.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);
    List<PurchaseOrder> findBySchoolIdAndStatusOrderByCreatedAtDesc(Long schoolId, PurchaseOrderStatus status);
    List<PurchaseOrder> findByVendorIdAndSchoolIdOrderByCreatedAtDesc(Long vendorId, Long schoolId);
    Optional<PurchaseOrder> findByPoNumberAndSchoolId(String poNumber, Long schoolId);

    @Query("SELECT COUNT(po) FROM PurchaseOrder po WHERE po.schoolId = :schoolId AND po.poNumber LIKE :prefix%")
    Long countByPrefix(@Param("schoolId") Long schoolId, @Param("prefix") String prefix);

    @Query("SELECT po FROM PurchaseOrder po JOIN FETCH po.vendor WHERE po.schoolId = :schoolId AND po.status IN :statuses ORDER BY po.createdAt DESC")
    List<PurchaseOrder> findBySchoolIdAndStatusIn(@Param("schoolId") Long schoolId, @Param("statuses") List<PurchaseOrderStatus> statuses);
}
