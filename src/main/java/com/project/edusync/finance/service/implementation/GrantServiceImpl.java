package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.grant.*;
import com.project.edusync.finance.model.entity.*;
import com.project.edusync.finance.model.enums.GrantStatus;
import com.project.edusync.finance.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Grant management service.
 *
 * Handles grant lifecycle (APPLIED → SANCTIONED → ACTIVE → UTILISED → CLOSED/LAPSED)
 * and utilisation recording with automatic balance tracking and GL integration.
 *
 * Key safeguards:
 *  - Cannot utilise more than the available balance.
 *  - Auto-transitions status (PARTIALLY_UTILISED → FULLY_UTILISED when utilisedAmount >= receivedAmount).
 *  - Expiry alerts for grants nearing end date (90 days window).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GrantServiceImpl {

    private final GrantRepository grantRepository;
    private final GrantUtilizationRepository utilisationRepository;
    private final AccountRepository accountRepository;

    // ── Grant CRUD ────────────────────────────────────────────────────────────

    public GrantResponseDTO createGrant(GrantRequestDTO dto, Long schoolId) {
        Grant grant = new Grant();
        mapDto(dto, grant, schoolId);
        grant.setReceivedAmount(dto.receivedAmount() != null ? dto.receivedAmount() : BigDecimal.ZERO);
        grant.setUtilisedAmount(BigDecimal.ZERO);
        if (grant.getStatus() == null) grant.setStatus(GrantStatus.APPLIED);
        return toDTO(grantRepository.save(grant));
    }

    public GrantResponseDTO updateGrant(Long id, GrantRequestDTO dto, Long schoolId) {
        Grant grant = findGrant(id, schoolId);
        mapDto(dto, grant, schoolId);
        return toDTO(grantRepository.save(grant));
    }

    @Transactional(readOnly = true)
    public List<GrantResponseDTO> getAllGrants(Long schoolId) {
        return grantRepository.findBySchoolIdOrderByStartDateDesc(schoolId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GrantResponseDTO getGrantById(Long id, Long schoolId) {
        return toDTO(findGrant(id, schoolId));
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    public GrantResponseDTO activateGrant(Long id, BigDecimal receivedAmount, Long schoolId) {
        Grant grant = findGrant(id, schoolId);
        if (grant.getStatus() != GrantStatus.SANCTIONED) throw new IllegalStateException("Only SANCTIONED grants can be activated.");
        grant.setStatus(GrantStatus.ACTIVE);
        grant.setReceivedAmount(receivedAmount != null ? receivedAmount : grant.getSanctionedAmount());
        return toDTO(grantRepository.save(grant));
    }

    public GrantResponseDTO closeGrant(Long id, Long schoolId) {
        Grant grant = findGrant(id, schoolId);
        grant.setStatus(GrantStatus.CLOSED);
        return toDTO(grantRepository.save(grant));
    }

    public GrantResponseDTO lapsGrant(Long id, Long schoolId) {
        Grant grant = findGrant(id, schoolId);
        grant.setStatus(GrantStatus.LAPSED);
        return toDTO(grantRepository.save(grant));
    }

    public GrantResponseDTO updateStatus(Long id, GrantStatus status, Long schoolId) {
        Grant grant = findGrant(id, schoolId);
        grant.setStatus(status);
        return toDTO(grantRepository.save(grant));
    }

    // ── Utilisation ───────────────────────────────────────────────────────────

    public GrantUtilizationResponseDTO recordUtilisation(GrantUtilizationRequestDTO dto, Long schoolId) {
        Grant grant = findGrant(dto.grantId(), schoolId);
        if (grant.getStatus() != GrantStatus.ACTIVE && grant.getStatus() != GrantStatus.PARTIALLY_UTILISED) {
            throw new IllegalStateException("Can only record utilisation for ACTIVE or PARTIALLY_UTILISED grants.");
        }
        if (dto.amount().compareTo(grant.getAvailableBalance()) > 0) {
            throw new IllegalStateException(
                    String.format("Utilisation of ₹%.2f exceeds available balance of ₹%.2f.",
                            dto.amount(), grant.getAvailableBalance()));
        }

        GrantUtilization util = new GrantUtilization();
        util.setGrant(grant);
        util.setUtilisationDate(dto.utilisationDate());
        util.setDescription(dto.description());
        util.setExpenseCategory(dto.expenseCategory());
        util.setAmount(dto.amount());
        util.setReferenceDocument(dto.referenceDocument());
        util.setApprovedBy(getCurrentUsername());
        util.setNotes(dto.notes());
        util.setSchoolId(schoolId);
        utilisationRepository.save(util);

        // Update grant utilised amount
        grant.setUtilisedAmount(grant.getUtilisedAmount().add(dto.amount()));
        // Auto status transition
        if (grant.getUtilisedAmount().compareTo(grant.getReceivedAmount()) >= 0) {
            grant.setStatus(GrantStatus.FULLY_UTILISED);
        } else if (grant.getStatus() == GrantStatus.ACTIVE) {
            grant.setStatus(GrantStatus.PARTIALLY_UTILISED);
        }
        grantRepository.save(grant);

        return toUtilDTO(util);
    }

    @Transactional(readOnly = true)
    public List<GrantUtilizationResponseDTO> getUtilisations(Long grantId, Long schoolId) {
        findGrant(grantId, schoolId);
        return utilisationRepository.findByGrantIdOrderByUtilisationDateDesc(grantId)
                .stream().map(this::toUtilDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GrantResponseDTO> getGrantsNearingExpiry(Long schoolId) {
        return grantRepository.findGrantsNearingExpiry(schoolId, LocalDate.now(), LocalDate.now().plusDays(90))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private Grant findGrant(Long id, Long schoolId) {
        Grant g = grantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Grant not found: " + id));
        if (!schoolId.equals(g.getSchoolId())) throw new EntityNotFoundException("Grant not found: " + id);
        return g;
    }

    private void mapDto(GrantRequestDTO dto, Grant grant, Long schoolId) {
        grant.setGrantingAgency(dto.grantingAgency()); grant.setGrantTitle(dto.grantTitle());
        grant.setGrantReference(dto.grantReference()); grant.setPrincipalInvestigator(dto.principalInvestigator());
        grant.setDepartment(dto.department()); grant.setSanctionedAmount(dto.sanctionedAmount());
        grant.setStartDate(dto.startDate()); grant.setEndDate(dto.endDate());
        if (dto.status() != null) grant.setStatus(dto.status());
        grant.setComplianceDueDate(dto.complianceDueDate()); grant.setObjectives(dto.objectives());
        grant.setNotes(dto.notes()); grant.setSchoolId(schoolId);

        if (dto.incomeAccountId() != null) accountRepository.findById(dto.incomeAccountId()).ifPresent(grant::setIncomeAccount);
    }

    private String getCurrentUsername() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); } catch (Exception e) { return "system"; }
    }

    public GrantResponseDTO toDTO(Grant g) {
        double utilPct = g.getReceivedAmount().compareTo(BigDecimal.ZERO) == 0 ? 0.0
                : g.getUtilisedAmount().multiply(BigDecimal.valueOf(100))
                    .divide(g.getReceivedAmount(), 1, RoundingMode.HALF_UP).doubleValue();
        boolean nearingExpiry = g.getEndDate() != null &&
                g.getEndDate().isAfter(LocalDate.now()) &&
                g.getEndDate().isBefore(LocalDate.now().plusDays(90)) &&
                (g.getStatus() == GrantStatus.ACTIVE || g.getStatus() == GrantStatus.PARTIALLY_UTILISED);
        boolean complianceOverdue = g.getComplianceDueDate() != null &&
                LocalDate.now().isAfter(g.getComplianceDueDate()) &&
                g.getStatus() != GrantStatus.CLOSED;

        List<GrantUtilizationResponseDTO> utils = g.getUtilisations().stream()
                .map(this::toUtilDTO).collect(Collectors.toList());

        return new GrantResponseDTO(g.getId(), g.getUuid(),
                g.getGrantingAgency(), g.getGrantTitle(), g.getGrantReference(),
                g.getPrincipalInvestigator(), g.getDepartment(),
                g.getSanctionedAmount(), g.getReceivedAmount(),
                g.getUtilisedAmount(), g.getAvailableBalance(), utilPct,
                g.getStartDate(), g.getEndDate(), g.getStatus(),
                g.getIncomeAccount() != null ? g.getIncomeAccount().getId() : null,
                g.getIncomeAccount() != null ? g.getIncomeAccount().getCode() : null,
                g.getIncomeAccount() != null ? g.getIncomeAccount().getName() : null,
                g.getComplianceDueDate(), nearingExpiry, complianceOverdue,
                g.getObjectives(), g.getNotes(), utils,
                g.getCreatedAt(), g.getCreatedBy());
    }

    public GrantUtilizationResponseDTO toUtilDTO(GrantUtilization u) {
        return new GrantUtilizationResponseDTO(u.getId(), u.getGrant().getId(), u.getGrant().getGrantTitle(),
                u.getUtilisationDate(), u.getDescription(), u.getExpenseCategory(),
                u.getAmount(), u.getReferenceDocument(), u.getGlEntryId(), u.getApprovedBy(), u.getNotes(),
                u.getCreatedAt());
    }
}
