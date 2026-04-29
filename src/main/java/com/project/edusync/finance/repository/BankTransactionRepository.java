package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.BankTransaction;
import com.project.edusync.finance.model.enums.ReconciliationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findByBankAccountIdOrderByTransactionDateDesc(Long bankAccountId);
    List<BankTransaction> findByBankAccountIdAndReconciliationStatusOrderByTransactionDateDesc(Long bankAccountId, ReconciliationStatus status);
    List<BankTransaction> findByBankAccountIdAndTransactionDateBetweenOrderByTransactionDateAsc(Long bankAccountId, LocalDate from, LocalDate to);

    @Query("SELECT COUNT(t) FROM BankTransaction t WHERE t.bankAccount.id = :bankAccountId AND t.reconciliationStatus = 'UNRECONCILED'")
    Long countUnreconciled(@Param("bankAccountId") Long bankAccountId);

    @Query("SELECT COALESCE(SUM(CASE WHEN t.transactionType = 'CREDIT' THEN t.amount ELSE -t.amount END), 0) FROM BankTransaction t WHERE t.bankAccount.id = :bankAccountId AND t.reconciliationStatus != 'UNRECONCILED'")
    BigDecimal sumReconciledBalance(@Param("bankAccountId") Long bankAccountId);

    /** Find transactions on same date with same amount — for auto-match. */
    @Query("SELECT t FROM BankTransaction t WHERE t.bankAccount.id = :bankAccountId AND t.transactionDate = :date AND t.amount = :amount AND t.reconciliationStatus = 'UNRECONCILED'")
    List<BankTransaction> findCandidatesForAutoMatch(@Param("bankAccountId") Long bankAccountId, @Param("date") LocalDate date, @Param("amount") BigDecimal amount);
}
