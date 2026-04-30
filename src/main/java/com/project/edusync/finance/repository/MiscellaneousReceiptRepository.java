package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.MiscellaneousReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MiscellaneousReceiptRepository extends JpaRepository<MiscellaneousReceipt, Long> {
    
    List<MiscellaneousReceipt> findBySchoolIdOrderByReceiptDateDesc(Long schoolId);
    
    Optional<MiscellaneousReceipt> findByIdAndSchoolId(Long id, Long schoolId);
    
    @Query("SELECT COUNT(mr) FROM MiscellaneousReceipt mr WHERE mr.schoolId = :schoolId AND mr.receiptNumber LIKE :prefix%")
    Long countByPrefix(@Param("schoolId") Long schoolId, @Param("prefix") String prefix);
    
}
