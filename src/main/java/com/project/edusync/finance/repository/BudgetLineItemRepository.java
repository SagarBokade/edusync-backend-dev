package com.project.edusync.finance.repository;

import com.project.edusync.finance.model.entity.BudgetLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface BudgetLineItemRepository extends JpaRepository<BudgetLineItem, Long> {

    /** All line items for a budget, ordered for display. */
    List<BudgetLineItem> findByBudgetIdOrderByLineNumber(Long budgetId);

    /** All line items that are linked to a specific COA account (for GL-driven actuals update). */
    List<BudgetLineItem> findByLinkedAccountId(Long accountId);

    /**
     * Updates the actualAmount for all line items linked to a given COA account
     * by refreshing it from the current account balance.
     * Called after every GL posting to that account.
     */
    @Modifying
    @Query("""
        UPDATE BudgetLineItem bli
        SET bli.actualAmount = :newActual,
            bli.variance = bli.allocatedAmount - :newActual
        WHERE bli.linkedAccount.id = :accountId
    """)
    int updateActualAmountForAccount(@Param("accountId") Long accountId, @Param("newActual") BigDecimal newActual);
}
