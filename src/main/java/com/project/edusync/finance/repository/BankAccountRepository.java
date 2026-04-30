package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findBySchoolIdAndIsActiveTrueOrderByAccountNameAsc(Long schoolId);
    List<BankAccount> findBySchoolIdOrderByAccountNameAsc(Long schoolId);
}
