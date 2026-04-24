package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.VendorBill;
import com.project.edusync.finance.model.enums.VendorBillStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
    List<VendorBill> findBySchoolIdOrderByBillDateDesc(Long schoolId);
    List<VendorBill> findBySchoolIdAndStatusOrderByBillDateDesc(Long schoolId, VendorBillStatus status);
    List<VendorBill> findByVendorIdAndSchoolIdOrderByBillDateDesc(Long vendorId, Long schoolId);
    List<VendorBill> findByPurchaseOrderIdOrderByBillDateDesc(Long purchaseOrderId);

    /** Bills overdue for payment (status = APPROVED_FOR_PAYMENT and dueDate < today). */
    @Query("SELECT vb FROM VendorBill vb WHERE vb.schoolId = :schoolId AND vb.status = 'APPROVED_FOR_PAYMENT' AND vb.dueDate < :today")
    List<VendorBill> findOverdueBills(@Param("schoolId") Long schoolId, @Param("today") LocalDate today);

    /** Total outstanding payables (approved for payment, not yet paid). */
    @Query("SELECT COALESCE(SUM(vb.totalPayable), 0) FROM VendorBill vb WHERE vb.schoolId = :schoolId AND vb.status = 'APPROVED_FOR_PAYMENT'")
    BigDecimal sumOutstandingPayables(@Param("schoolId") Long schoolId);

    @Query("SELECT COUNT(vb) FROM VendorBill vb WHERE vb.schoolId = :schoolId AND vb.billNumber LIKE :prefix%")
    Long countByPrefix(@Param("schoolId") Long schoolId, @Param("prefix") String prefix);

    @Query("SELECT COUNT(vb) FROM VendorBill vb WHERE vb.schoolId = :schoolId AND vb.status = :status")
    Long countBySchoolIdAndStatus(@Param("schoolId") Long schoolId, @Param("status") VendorBillStatus status);
}
