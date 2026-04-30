package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.account.AccountRequestDTO;
import com.project.edusync.finance.dto.account.AccountResponseDTO;
import com.project.edusync.finance.model.entity.Account;
import com.project.edusync.finance.model.enums.AccountType;
import com.project.edusync.finance.repository.AccountRepository;
import com.project.edusync.finance.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO dto, Long schoolId) {
        if (accountRepository.existsByCodeAndSchoolId(dto.code(), schoolId)) {
            throw new IllegalArgumentException("Account code '" + dto.code() + "' already exists.");
        }
        Account account = new Account();
        mapDtoToEntity(dto, account, schoolId);
        return toResponseDTO(accountRepository.save(account), false);
    }

    @Override
    public AccountResponseDTO updateAccount(Long accountId, AccountRequestDTO dto, Long schoolId) {
        Account account = findAccount(accountId, schoolId);
        if (accountRepository.existsByCodeAndSchoolIdAndIdNot(dto.code(), schoolId, accountId)) {
            throw new IllegalArgumentException("Account code '" + dto.code() + "' is already used by another account.");
        }
        mapDtoToEntity(dto, account, schoolId);
        return toResponseDTO(accountRepository.save(account), false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getCOATree(Long schoolId) {
        List<Account> roots = accountRepository.findByParentAccountIsNullAndSchoolIdOrderByCode(schoolId);
        return roots.stream()
                .map(root -> toResponseDTOWithChildren(root))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts(Long schoolId) {
        return accountRepository.findAll().stream()
                .filter(a -> schoolId.equals(a.getSchoolId()))
                .map(a -> toResponseDTO(a, false))
                .sorted((a, b) -> a.code().compareTo(b.code()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getPostingAccounts(Long schoolId) {
        return accountRepository.findByActiveAndPostingAccountAndSchoolIdOrderByCode(true, true, schoolId)
                .stream()
                .map(a -> toResponseDTO(a, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(Long accountId, Long schoolId) {
        return toResponseDTO(findAccount(accountId, schoolId), false);
    }

    @Override
    public void deactivateAccount(Long accountId, Long schoolId) {
        Account account = findAccount(accountId, schoolId);
        account.setActive(false);
        accountRepository.save(account);
    }

    @Override
    public void seedDefaultCOA(Long schoolId) {
        if (accountRepository.existsByCodeAndSchoolId("1000", schoolId)) {
            return; // Already seeded
        }

        // ── Root Groups ────────────────────────────────────────────────
        Account assets    = createGroup("1000", "Assets",      AccountType.ASSET,    null, schoolId);
        Account liabs     = createGroup("2000", "Liabilities", AccountType.LIABILITY, null, schoolId);
        Account equity    = createGroup("3000", "Equity",      AccountType.EQUITY,   null, schoolId);
        Account income    = createGroup("4000", "Income",      AccountType.INCOME,   null, schoolId);
        Account expenses  = createGroup("5000", "Expenses",    AccountType.EXPENSE,  null, schoolId);

        // ── Assets ─────────────────────────────────────────────────────
        Account currentAssets = createGroup("1100", "Current Assets", AccountType.ASSET, assets, schoolId);
        createPosting("1110", "Cash in Hand",         AccountType.ASSET, currentAssets, schoolId);
        createPosting("1120", "Bank — Main Account",  AccountType.ASSET, currentAssets, schoolId);
        createPosting("1130", "Online Gateway Float", AccountType.ASSET, currentAssets, schoolId);
        createPosting("1140", "Fee Receivables",      AccountType.ASSET, currentAssets, schoolId);
        createPosting("1150", "Grant Receivables",    AccountType.ASSET, currentAssets, schoolId);

        Account fixedAssets = createGroup("1200", "Fixed Assets", AccountType.ASSET, assets, schoolId);
        createPosting("1210", "Lab Equipment",      AccountType.ASSET, fixedAssets, schoolId);
        createPosting("1220", "Furniture",          AccountType.ASSET, fixedAssets, schoolId);
        createPosting("1230", "Vehicles",           AccountType.ASSET, fixedAssets, schoolId);
        createPosting("1240", "Buildings",          AccountType.ASSET, fixedAssets, schoolId);
        createPosting("1250", "IT Hardware",        AccountType.ASSET, fixedAssets, schoolId);

        Account accumDep = createGroup("1300", "Accumulated Depreciation", AccountType.ASSET, assets, schoolId);
        createPosting("1310", "Accum. Dep. — Equipment", AccountType.ASSET, accumDep, schoolId);
        createPosting("1320", "Accum. Dep. — Furniture",  AccountType.ASSET, accumDep, schoolId);

        // ── Liabilities ────────────────────────────────────────────────
        Account currentLiabs = createGroup("2100", "Current Liabilities", AccountType.LIABILITY, liabs, schoolId);
        createPosting("2110", "Accounts Payable",      AccountType.LIABILITY, currentLiabs, schoolId);
        createPosting("2120", "EPF Payable",           AccountType.LIABILITY, currentLiabs, schoolId);
        createPosting("2130", "ESI Payable",           AccountType.LIABILITY, currentLiabs, schoolId);
        createPosting("2140", "TDS Payable",           AccountType.LIABILITY, currentLiabs, schoolId);
        createPosting("2150", "Professional Tax Payable", AccountType.LIABILITY, currentLiabs, schoolId);
        createPosting("2160", "GST Payable",           AccountType.LIABILITY, currentLiabs, schoolId);
        createPosting("2170", "Refunds Payable",       AccountType.LIABILITY, currentLiabs, schoolId);

        // ── Equity ─────────────────────────────────────────────────────
        createPosting("3100", "Corpus Fund",         AccountType.EQUITY, equity, schoolId);
        createPosting("3200", "Retained Surplus",    AccountType.EQUITY, equity, schoolId);

        // ── Income ─────────────────────────────────────────────────────
        Account feeIncome   = createGroup("4100", "Fee Income",   AccountType.INCOME, income, schoolId);
        createPosting("4110", "Tuition Fee Revenue",   AccountType.INCOME, feeIncome, schoolId);
        createPosting("4120", "Hostel Fee Revenue",    AccountType.INCOME, feeIncome, schoolId);
        createPosting("4130", "Transport Fee Revenue", AccountType.INCOME, feeIncome, schoolId);
        createPosting("4140", "Exam Fee Revenue",      AccountType.INCOME, feeIncome, schoolId);
        createPosting("4150", "Late Fee Income",       AccountType.INCOME, feeIncome, schoolId);

        Account otherIncome = createGroup("4200", "Other Income", AccountType.INCOME, income, schoolId);
        createPosting("4210", "Grant Income",          AccountType.INCOME, otherIncome, schoolId);
        createPosting("4220", "Auditorium / Hall Rental", AccountType.INCOME, otherIncome, schoolId);
        createPosting("4230", "Interest Income",       AccountType.INCOME, otherIncome, schoolId);
        createPosting("4240", "Sponsorship Income",    AccountType.INCOME, otherIncome, schoolId);
        createPosting("4250", "Canteen Revenue",       AccountType.INCOME, otherIncome, schoolId);

        // ── Expenses ───────────────────────────────────────────────────
        Account hrExp   = createGroup("5100", "Human Resource Expenses", AccountType.EXPENSE, expenses, schoolId);
        createPosting("5110", "Faculty Salary",        AccountType.EXPENSE, hrExp, schoolId);
        createPosting("5120", "Staff Salary",          AccountType.EXPENSE, hrExp, schoolId);
        createPosting("5130", "EPF Contribution",      AccountType.EXPENSE, hrExp, schoolId);
        createPosting("5140", "ESI Contribution",      AccountType.EXPENSE, hrExp, schoolId);
        createPosting("5150", "Gratuity Expense",      AccountType.EXPENSE, hrExp, schoolId);

        Account acadExp = createGroup("5200", "Academic Expenses", AccountType.EXPENSE, expenses, schoolId);
        createPosting("5210", "Lab Supplies",          AccountType.EXPENSE, acadExp, schoolId);
        createPosting("5220", "Library Books",         AccountType.EXPENSE, acadExp, schoolId);
        createPosting("5230", "Research Expense",      AccountType.EXPENSE, acadExp, schoolId);

        Account infraExp = createGroup("5300", "Infrastructure Expenses", AccountType.EXPENSE, expenses, schoolId);
        createPosting("5310", "Electricity Bill",      AccountType.EXPENSE, infraExp, schoolId);
        createPosting("5320", "Internet & Telecom",    AccountType.EXPENSE, infraExp, schoolId);
        createPosting("5330", "Building Maintenance",  AccountType.EXPENSE, infraExp, schoolId);
        createPosting("5340", "Equipment Maintenance", AccountType.EXPENSE, infraExp, schoolId);

        Account adminExp = createGroup("5400", "Administrative Expenses", AccountType.EXPENSE, expenses, schoolId);
        createPosting("5410", "Office Stationery",     AccountType.EXPENSE, adminExp, schoolId);
        createPosting("5420", "Printing & Postage",    AccountType.EXPENSE, adminExp, schoolId);
        createPosting("5430", "Travel & Conveyance",   AccountType.EXPENSE, adminExp, schoolId);
        createPosting("5440", "Bank Charges",          AccountType.EXPENSE, adminExp, schoolId);
        createPosting("5450", "Depreciation Expense",  AccountType.EXPENSE, adminExp, schoolId);
    }

    // ── Private Helpers ─────────────────────────────────────────────────────

    private Account createGroup(String code, String name, AccountType type, Account parent, Long schoolId) {
        Account a = new Account();
        a.setCode(code);
        a.setName(name);
        a.setAccountType(type);
        a.setParentAccount(parent);
        a.setPostingAccount(false);
        a.setActive(true);
        a.setBalance(BigDecimal.ZERO);
        a.setSchoolId(schoolId);
        return accountRepository.save(a);
    }

    private Account createPosting(String code, String name, AccountType type, Account parent, Long schoolId) {
        Account a = new Account();
        a.setCode(code);
        a.setName(name);
        a.setAccountType(type);
        a.setParentAccount(parent);
        a.setPostingAccount(true);
        a.setActive(true);
        a.setBalance(BigDecimal.ZERO);
        a.setSchoolId(schoolId);
        return accountRepository.save(a);
    }

    private void mapDtoToEntity(AccountRequestDTO dto, Account account, Long schoolId) {
        account.setCode(dto.code());
        account.setName(dto.name());
        account.setAccountType(dto.accountType());
        account.setDescription(dto.description());
        account.setPostingAccount(dto.postingAccount());
        account.setActive(dto.active());
        account.setSchoolId(schoolId);
        if (dto.parentAccountId() != null) {
            account.setParentAccount(accountRepository.findById(dto.parentAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent account not found: " + dto.parentAccountId())));
        } else {
            account.setParentAccount(null);
        }
    }

    private Account findAccount(Long id, Long schoolId) {
        Account a = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + id));
        if (!schoolId.equals(a.getSchoolId())) {
            throw new EntityNotFoundException("Account not found: " + id);
        }
        return a;
    }

    private AccountResponseDTO toResponseDTO(Account account, boolean withChildren) {
        List<AccountResponseDTO> children = withChildren
                ? account.getChildren().stream().map(c -> toResponseDTOWithChildren(c)).collect(Collectors.toList())
                : new ArrayList<>();
        return new AccountResponseDTO(
                account.getId(),
                account.getUuid(),
                account.getCode(),
                account.getName(),
                account.getAccountType(),
                account.getParentAccount() != null ? account.getParentAccount().getId() : null,
                account.getParentAccount() != null ? account.getParentAccount().getName() : null,
                account.getDescription(),
                account.getBalance(),
                account.isPostingAccount(),
                account.isActive(),
                children
        );
    }

    private AccountResponseDTO toResponseDTOWithChildren(Account account) {
        List<AccountResponseDTO> children = account.getChildren().stream()
                .map(this::toResponseDTOWithChildren)
                .sorted((a, b) -> a.code().compareTo(b.code()))
                .collect(Collectors.toList());
        return new AccountResponseDTO(
                account.getId(),
                account.getUuid(),
                account.getCode(),
                account.getName(),
                account.getAccountType(),
                account.getParentAccount() != null ? account.getParentAccount().getId() : null,
                account.getParentAccount() != null ? account.getParentAccount().getName() : null,
                account.getDescription(),
                account.getBalance(),
                account.isPostingAccount(),
                account.isActive(),
                children
        );
    }
}
