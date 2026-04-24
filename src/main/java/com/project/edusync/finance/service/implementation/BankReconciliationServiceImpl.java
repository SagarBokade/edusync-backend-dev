package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.bank.BankAccountResponseDTO;
import com.project.edusync.finance.dto.bank.BankTransactionRequestDTO;
import com.project.edusync.finance.dto.bank.BankTransactionResponseDTO;
import com.project.edusync.finance.model.entity.*;
import com.project.edusync.finance.model.enums.ReconciliationStatus;
import com.project.edusync.finance.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bank Reconciliation service.
 *
 * Flow:
 * 1. Finance Admin defines BankAccounts.
 * 2. Bank statement transactions are imported (manually or via CSV — future).
 * 3. Auto-match engine tries to pair each bank transaction with a GL entry
 *    on the same date ± 3 days with the same amount.
 * 4. Remaining unmatched items are flagged for manual review.
 * 5. Summary shows: Bank Balance vs GL Book Balance and the difference.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BankReconciliationServiceImpl {

    private final BankAccountRepository bankAccountRepository;
    private final BankTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final JournalEntryRepository journalEntryRepository;

    // ── Bank Accounts ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BankAccountResponseDTO> getAllBankAccounts(Long schoolId) {
        return bankAccountRepository.findBySchoolIdOrderByAccountNameAsc(schoolId)
                .stream().map(ba -> toBankAccountDTO(ba, schoolId)).collect(Collectors.toList());
    }

    public BankAccountResponseDTO createBankAccount(String accountName, String accountNumber,
            String bankName, String branchName, String ifscCode, String accountType,
            Long coaAccountId, String notes, Long schoolId) {
        BankAccount ba = new BankAccount();
        ba.setAccountName(accountName); ba.setAccountNumber(accountNumber);
        ba.setBankName(bankName); ba.setBranchName(branchName);
        ba.setIfscCode(ifscCode); ba.setAccountType(accountType);
        ba.setNotes(notes); ba.setSchoolId(schoolId);
        ba.setIsActive(true); ba.setStatementBalance(BigDecimal.ZERO); ba.setBookBalance(BigDecimal.ZERO);

        if (coaAccountId != null) {
            accountRepository.findById(coaAccountId).ifPresent(ba::setCoaAccount);
        }
        return toBankAccountDTO(bankAccountRepository.save(ba), schoolId);
    }

    // ── Transactions ──────────────────────────────────────────────────────────

    public BankTransactionResponseDTO addTransaction(@Valid BankTransactionRequestDTO dto, Long schoolId) {
        BankAccount ba = bankAccountRepository.findById(dto.bankAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Bank Account not found: " + dto.bankAccountId()));

        BankTransaction tx = new BankTransaction();
        tx.setBankAccount(ba);
        tx.setTransactionDate(dto.transactionDate());
        tx.setValueDate(dto.valueDate());
        tx.setDescription(dto.description());
        tx.setReferenceNumber(dto.referenceNumber());
        tx.setInstrumentNumber(dto.instrumentNumber());
        tx.setTransactionType(dto.transactionType());
        tx.setAmount(dto.amount());
        tx.setRunningBalance(dto.runningBalance());
        tx.setReconciliationStatus(ReconciliationStatus.UNRECONCILED);
        tx.setSchoolId(schoolId);

        // Update bank account statement balance
        if (dto.transactionType().name().equals("CREDIT")) {
            ba.setStatementBalance(ba.getStatementBalance().add(dto.amount()));
        } else {
            ba.setStatementBalance(ba.getStatementBalance().subtract(dto.amount()));
        }
        bankAccountRepository.save(ba);
        return toTxDTO(transactionRepository.save(tx));
    }

    @Transactional(readOnly = true)
    public List<BankTransactionResponseDTO> getTransactions(Long bankAccountId, String statusParam) {
        if (statusParam != null) {
            ReconciliationStatus status = ReconciliationStatus.valueOf(statusParam);
            return transactionRepository.findByBankAccountIdAndReconciliationStatusOrderByTransactionDateDesc(bankAccountId, status)
                    .stream().map(this::toTxDTO).collect(Collectors.toList());
        }
        return transactionRepository.findByBankAccountIdOrderByTransactionDateDesc(bankAccountId)
                .stream().map(this::toTxDTO).collect(Collectors.toList());
    }

    // ── Reconciliation Engine ─────────────────────────────────────────────────

    /**
     * Auto-match all UNRECONCILED transactions for a bank account.
     * Looks at same date and same amount. Returns count of newly matched transactions.
     */
    public int runAutoMatch(Long bankAccountId, Long schoolId) {
        List<BankTransaction> unreconciled = transactionRepository
                .findByBankAccountIdAndReconciliationStatusOrderByTransactionDateDesc(bankAccountId, ReconciliationStatus.UNRECONCILED);

        int matched = 0;
        for (BankTransaction tx : unreconciled) {
            // Try to find a GL entry on the same date with the same amount
            List<JournalEntry> candidates = journalEntryRepository.findByDateAndAmount(
                    tx.getTransactionDate(), tx.getAmount(), schoolId);

            if (!candidates.isEmpty()) {
                JournalEntry gl = candidates.get(0); // take first match
                tx.setReconciliationStatus(ReconciliationStatus.AUTO_MATCHED);
                tx.setMatchedGlEntryId(gl.getId());
                tx.setReconciliationNotes("Auto-matched to GL entry " + gl.getEntryNumber());
                transactionRepository.save(tx);
                matched++;
            }
        }
        log.info("Auto-match complete for bank account {}: {} of {} transactions matched.", bankAccountId, matched, unreconciled.size());
        return matched;
    }

    /** Manually match a bank transaction to a GL entry. */
    public BankTransactionResponseDTO manualMatch(Long txId, Long glEntryId, String notes, Long schoolId) {
        BankTransaction tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + txId));
        tx.setReconciliationStatus(ReconciliationStatus.MANUALLY_MATCHED);
        tx.setMatchedGlEntryId(glEntryId);
        tx.setReconciliationNotes(notes != null ? notes : "Manually matched");
        return toTxDTO(transactionRepository.save(tx));
    }

    /** Flag a transaction as an exception (can't be matched). */
    public BankTransactionResponseDTO flagException(Long txId, String reason) {
        BankTransaction tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + txId));
        tx.setReconciliationStatus(ReconciliationStatus.EXCEPTION);
        tx.setReconciliationNotes(reason);
        return toTxDTO(transactionRepository.save(tx));
    }

    /** Ignore a transaction (duplicate, bank charge, etc.). */
    public BankTransactionResponseDTO ignore(Long txId) {
        BankTransaction tx = transactionRepository.findById(txId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found: " + txId));
        tx.setReconciliationStatus(ReconciliationStatus.IGNORED);
        return toTxDTO(transactionRepository.save(tx));
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private BankAccountResponseDTO toBankAccountDTO(BankAccount ba, Long schoolId) {
        Long unreconciled = transactionRepository.countUnreconciled(ba.getId());
        BigDecimal diff = ba.getStatementBalance().subtract(ba.getBookBalance());
        return new BankAccountResponseDTO(ba.getId(), ba.getUuid(), ba.getAccountName(),
                ba.getAccountNumber(), ba.getBankName(), ba.getBranchName(), ba.getIfscCode(), ba.getAccountType(),
                ba.getCoaAccount() != null ? ba.getCoaAccount().getId() : null,
                ba.getCoaAccount() != null ? ba.getCoaAccount().getCode() : null,
                ba.getCoaAccount() != null ? ba.getCoaAccount().getName() : null,
                ba.getStatementBalance(), ba.getBookBalance(), diff, unreconciled,
                ba.getIsActive(), ba.getNotes(), ba.getCreatedAt());
    }

    private BankTransactionResponseDTO toTxDTO(BankTransaction tx) {
        return new BankTransactionResponseDTO(tx.getId(), tx.getBankAccount().getId(),
                tx.getBankAccount().getAccountName(),
                tx.getTransactionDate(), tx.getValueDate(), tx.getDescription(),
                tx.getReferenceNumber(), tx.getInstrumentNumber(),
                tx.getTransactionType(), tx.getAmount(), tx.getRunningBalance(),
                tx.getReconciliationStatus(), tx.getMatchedGlEntryId(), tx.getReconciliationNotes(),
                tx.getCreatedAt());
    }
}
