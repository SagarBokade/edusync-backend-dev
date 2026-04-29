package com.project.edusync.finance.dto.account;

import com.project.edusync.finance.model.enums.AccountType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a Chart of Accounts node.
 * Returns a nested tree structure when used for the full COA tree view.
 */
public record AccountResponseDTO(
    Long id,
    UUID uuid,
    String code,
    String name,
    AccountType accountType,
    Long parentAccountId,
    String parentAccountName,
    String description,
    BigDecimal balance,
    boolean postingAccount,
    boolean active,
    /** Recursively nested children — populated in tree view, empty in flat list view. */
    List<AccountResponseDTO> children
) {}
