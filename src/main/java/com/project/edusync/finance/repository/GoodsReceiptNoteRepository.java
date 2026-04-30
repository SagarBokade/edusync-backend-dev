package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.GoodsReceiptNote;
import com.project.edusync.finance.model.enums.GRNStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsReceiptNoteRepository extends JpaRepository<GoodsReceiptNote, Long> {
    List<GoodsReceiptNote> findBySchoolIdOrderByReceiptDateDesc(Long schoolId);
    List<GoodsReceiptNote> findByPurchaseOrderIdOrderByReceiptDateDesc(Long purchaseOrderId);
    List<GoodsReceiptNote> findBySchoolIdAndStatusOrderByReceiptDateDesc(Long schoolId, GRNStatus status);

    @Query("SELECT COUNT(g) FROM GoodsReceiptNote g WHERE g.schoolId = :schoolId AND g.grnNumber LIKE :prefix%")
    Long countByPrefix(@Param("schoolId") Long schoolId, @Param("prefix") String prefix);

    /** Check if any GRN has been created for a PO (blocks PO cancellation after goods received). */
    boolean existsByPurchaseOrderId(Long purchaseOrderId);
}
