package com.project.edusync.finance.dto.account;

import com.project.edusync.finance.model.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating a Chart of Accounts entry.
 */
public record AccountRequestDTO(

    @NotBlank(message = "Account code is required")
    @Size(max = 20, message = "Code must be 20 characters or less")
    String code,

    @NotBlank(message = "Account name is required")
    @Size(max = 150, message = "Name must be 150 characters or less")
    String name,

    @NotNull(message = "Account type is required")
    AccountType accountType,

    /** ID of the parent account. Null for root-level accounts. */
    Long parentAccountId,

    String description,

    boolean postingAccount,

    boolean active
) {}
