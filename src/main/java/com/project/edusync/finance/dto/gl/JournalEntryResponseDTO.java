package com.project.edusync.finance.dto.gl;

import com.project.edusync.finance.model.enums.JournalEntryStatus;
import com.project.edusync.finance.model.enums.JournalReferenceType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a General Ledger Journal Entry.
 */
public record JournalEntryResponseDTO(
    Long id,
    UUID uuid,
    String entryNumber,
    LocalDate entryDate,
    String description,
    JournalReferenceType referenceType,
    Long referenceId,
    JournalEntryStatus status,
    String postedBy,
    Long reversalOfEntryId,
    /** Computed total debits — must equal totalCredits for a valid entry. */
    BigDecimal totalDebits,
    /** Computed total credits. */
    BigDecimal totalCredits,
    List<JournalLineResponseDTO> lines,
    LocalDateTime createdAt,
    String createdBy
) {}
