package com.project.edusync.finance.service;

import com.project.edusync.finance.dto.account.AccountRequestDTO;
import com.project.edusync.finance.dto.account.AccountResponseDTO;

import java.util.List;

public interface AccountService {

    /** Create a new account in the COA. */
    AccountResponseDTO createAccount(AccountRequestDTO dto, Long schoolId);

    /** Update an existing account (code, name, description, active status). */
    AccountResponseDTO updateAccount(Long accountId, AccountRequestDTO dto, Long schoolId);

    /** Fetch the full COA as a nested tree (root → children → grandchildren…). */
    List<AccountResponseDTO> getCOATree(Long schoolId);

    /** Flat list of all accounts — for data tables. */
    List<AccountResponseDTO> getAllAccounts(Long schoolId);

    /** Flat list of active posting accounts — for journal entry dropdowns. */
    List<AccountResponseDTO> getPostingAccounts(Long schoolId);

    /** Get a single account by ID. */
    AccountResponseDTO getAccountById(Long accountId, Long schoolId);

    /** Soft-delete by toggling active = false. */
    void deactivateAccount(Long accountId, Long schoolId);

    /**
     * Seeds a standard Indian educational institution COA for a new school.
     * Called once during school onboarding.
     */
    void seedDefaultCOA(Long schoolId);
}
