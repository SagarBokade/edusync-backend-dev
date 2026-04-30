package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.asset.AssetRequestDTO;
import com.project.edusync.finance.dto.asset.AssetResponseDTO;
import com.project.edusync.finance.dto.asset.DepreciationEntryResponseDTO;
import com.project.edusync.finance.model.entity.*;
import com.project.edusync.finance.model.enums.AssetStatus;
import com.project.edusync.finance.model.enums.DepreciationMethod;
import com.project.edusync.finance.model.enums.JournalReferenceType;
import com.project.edusync.finance.repository.*;
import com.project.edusync.finance.service.GeneralLedgerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fixed Asset Register + Depreciation Engine.
 *
 * Depreciation methods:
 *   STRAIGHT_LINE: depreciableAmount / usefulLifeYears
 *   WRITTEN_DOWN_VALUE: currentBookValue × (rate / 100)
 *   UNITS_OF_PRODUCTION: not implemented here (requires production input)
 *
 * GL on purchase:  Dr Asset Account (1xxx), Cr Accounts Payable / Bank (2110 / 1120)
 * GL on depreciation: Dr Depreciation Expense (5xxx), Cr Accumulated Depreciation (1xxx)
 * GL on disposal: Dr Accumulated Depreciation (1xxx) + Dr Loss / Cr Gain, Cr Asset Account
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FixedAssetServiceImpl {

    private final FixedAssetRepository assetRepository;
    private final DepreciationEntryRepository depreciationRepository;
    private final VendorRepository vendorRepository;
    private final AccountRepository accountRepository;
    private final GeneralLedgerService glService;

    private static final Long DEFAULT_SCHOOL = 1L;
    private static final String DEPRECIATION_EXP_CODE  = "5310"; // Depreciation Expense
    private static final String ACC_DEPRECIATION_CODE  = "1399"; // Accumulated Depreciation (contra asset)

    // ── Create / Update ───────────────────────────────────────────────────────

    public AssetResponseDTO createAsset(AssetRequestDTO dto, Long schoolId) {
        FixedAsset asset = new FixedAsset();
        mapDto(dto, asset, schoolId);
        asset.setAssetCode(generateCode(schoolId));
        asset.setCurrentBookValue(dto.purchaseCost());
        asset.setAccumulatedDepreciation(BigDecimal.ZERO);
        if (asset.getDepreciationMethod() == null) asset.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        if (asset.getStatus() == null) asset.setStatus(AssetStatus.IN_TRANSIT);
        return toDTO(assetRepository.save(asset));
    }

    public AssetResponseDTO updateAsset(Long id, AssetRequestDTO dto, Long schoolId) {
        FixedAsset asset = findAsset(id, schoolId);
        mapDto(dto, asset, schoolId);
        return toDTO(assetRepository.save(asset));
    }

    @Transactional(readOnly = true)
    public List<AssetResponseDTO> getAllAssets(Long schoolId) {
        return assetRepository.findBySchoolIdOrderByPurchaseDateDesc(schoolId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssetResponseDTO getAssetById(Long id, Long schoolId) {
        return toDTO(findAsset(id, schoolId));
    }

    // ── Depreciation ──────────────────────────────────────────────────────────

    /**
     * Post depreciation for a single asset for the given financial year.
     * Idempotent — if a Depreciation Entry already exists for this year, it is returned unchanged.
     */
    public DepreciationEntryResponseDTO postDepreciation(Long assetId, String financialYear, Long schoolId) {
        FixedAsset asset = findAsset(assetId, schoolId);

        if (depreciationRepository.existsByAssetIdAndFinancialYear(assetId, financialYear)) {
            throw new IllegalStateException("Depreciation already posted for asset " + asset.getAssetCode() + " in " + financialYear);
        }
        if (asset.getStatus() != AssetStatus.ACTIVE) {
            throw new IllegalStateException("Can only depreciate ACTIVE assets. Current status: " + asset.getStatus());
        }
        if (asset.getCurrentBookValue().compareTo(asset.getSalvageValue()) <= 0) {
            throw new IllegalStateException("Asset " + asset.getAssetCode() + " is fully depreciated.");
        }

        BigDecimal charge = calcDepreciationCharge(asset);
        BigDecimal openingBV = asset.getCurrentBookValue();
        BigDecimal closingBV = openingBV.subtract(charge).max(asset.getSalvageValue());

        // Update asset
        asset.setAccumulatedDepreciation(asset.getAccumulatedDepreciation().add(charge));
        asset.setCurrentBookValue(closingBV);
        asset.setLastDepreciationDate(LocalDate.now());
        assetRepository.save(asset);

        // Create entry
        DepreciationEntry dep = new DepreciationEntry();
        dep.setAsset(asset);
        dep.setFinancialYear(financialYear);
        dep.setDepreciationDate(LocalDate.now());
        dep.setOpeningBookValue(openingBV);
        dep.setDepreciationAmount(charge);
        dep.setClosingBookValue(closingBV);
        dep.setSchoolId(schoolId);

        // Post GL entry
        try {
            var depExpOpt  = accountRepository.findByCodeAndSchoolId(DEPRECIATION_EXP_CODE, schoolId);
            var accDepOpt  = accountRepository.findByCodeAndSchoolId(ACC_DEPRECIATION_CODE, schoolId);
            if (depExpOpt.isPresent() && accDepOpt.isPresent()) {
                JournalEntry entry = glService.autoPostEntry(
                        LocalDate.now(),
                        "Depreciation — " + asset.getName() + " [" + asset.getAssetCode() + "] FY " + financialYear,
                        JournalReferenceType.DEPRECIATION,
                        assetId,
                        depExpOpt.get().getId(),
                        accDepOpt.get().getId(),
                        charge,
                        schoolId
                );
                dep.setGlEntryId(entry.getId());
            }
        } catch (Exception ex) {
            log.error("GL post failed for depreciation of {}: {}", asset.getAssetCode(), ex.getMessage());
        }

        return toDepDTO(depreciationRepository.save(dep));
    }

    /** Batch depreciation for all eligible assets in a financial year. */
    public List<DepreciationEntryResponseDTO> runDepreciationBatch(String financialYear, Long schoolId) {
        return assetRepository.findAssetsEligibleForDepreciation(schoolId).stream()
                .filter(a -> !depreciationRepository.existsByAssetIdAndFinancialYear(a.getId(), financialYear))
                .map(a -> postDepreciation(a.getId(), financialYear, schoolId))
                .collect(Collectors.toList());
    }

    /** Dispose / write off an asset. */
    public AssetResponseDTO disposeAsset(Long id, LocalDate disposalDate, String reason, BigDecimal proceeds, Long schoolId) {
        FixedAsset asset = findAsset(id, schoolId);
        if (asset.getStatus() == AssetStatus.DISPOSED || asset.getStatus() == AssetStatus.WRITTEN_OFF) {
            throw new IllegalStateException("Asset is already disposed/written off.");
        }
        asset.setStatus(proceeds != null && proceeds.compareTo(BigDecimal.ZERO) > 0 ? AssetStatus.DISPOSED : AssetStatus.WRITTEN_OFF);
        asset.setDisposalDate(disposalDate);
        asset.setDisposalReason(reason);
        asset.setDisposalProceeds(proceeds);
        return toDTO(assetRepository.save(asset));
    }

    @Transactional(readOnly = true)
    public List<DepreciationEntryResponseDTO> getDepreciationHistory(Long assetId, Long schoolId) {
        findAsset(assetId, schoolId);
        return depreciationRepository.findByAssetIdOrderByDepreciationDateDesc(assetId)
                .stream().map(this::toDepDTO).collect(Collectors.toList());
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private BigDecimal calcDepreciationCharge(FixedAsset asset) {
        return switch (asset.getDepreciationMethod()) {
            case STRAIGHT_LINE -> asset.getDepreciableAmount()
                    .divide(BigDecimal.valueOf(asset.getUsefulLifeYears()), 2, RoundingMode.HALF_UP);
            case WRITTEN_DOWN_VALUE -> {
                BigDecimal rate = asset.getDepreciationRatePct() != null
                        ? asset.getDepreciationRatePct()
                        : BigDecimal.valueOf(100).divide(BigDecimal.valueOf(asset.getUsefulLifeYears()), 2, RoundingMode.HALF_UP);
                yield asset.getCurrentBookValue().multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
            default -> asset.getDepreciableAmount()
                    .divide(BigDecimal.valueOf(asset.getUsefulLifeYears()), 2, RoundingMode.HALF_UP);
        };
    }

    private void mapDto(AssetRequestDTO dto, FixedAsset asset, Long schoolId) {
        asset.setName(dto.name()); asset.setAssetCategory(dto.assetCategory()); asset.setLocation(dto.location());
        asset.setDescription(dto.description()); asset.setMake(dto.make()); asset.setModel(dto.model());
        asset.setSerialNumber(dto.serialNumber()); asset.setPurchaseDate(dto.purchaseDate()); asset.setInUseDate(dto.inUseDate());
        asset.setPurchaseCost(dto.purchaseCost());
        asset.setSalvageValue(dto.salvageValue() != null ? dto.salvageValue() : BigDecimal.ZERO);
        asset.setUsefulLifeYears(dto.usefulLifeYears());
        asset.setDepreciationMethod(dto.depreciationMethod() != null ? dto.depreciationMethod() : DepreciationMethod.STRAIGHT_LINE);
        asset.setDepreciationRatePct(dto.depreciationRatePct());
        if (dto.status() != null) asset.setStatus(dto.status());
        asset.setNotes(dto.notes()); asset.setSchoolId(schoolId);

        if (dto.vendorId() != null) vendorRepository.findById(dto.vendorId()).ifPresent(asset::setVendor);
        if (dto.assetAccountId() != null) accountRepository.findById(dto.assetAccountId()).ifPresent(asset::setAssetAccount);
    }

    private FixedAsset findAsset(Long id, Long schoolId) {
        FixedAsset a = assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found: " + id));
        if (!schoolId.equals(a.getSchoolId())) throw new EntityNotFoundException("Asset not found: " + id);
        return a;
    }

    private String generateCode(Long schoolId) {
        int year = Year.now().getValue();
        String prefix = "ASSET-" + year + "-";
        long count = assetRepository.countByPrefix(schoolId, prefix);
        return prefix + String.format("%05d", count + 1);
    }

    public AssetResponseDTO toDTO(FixedAsset a) {
        return new AssetResponseDTO(a.getId(), a.getUuid(), a.getAssetCode(), a.getName(), a.getAssetCategory(),
                a.getLocation(), a.getDescription(), a.getMake(), a.getModel(), a.getSerialNumber(),
                a.getPurchaseDate(), a.getInUseDate(),
                a.getPurchaseCost(), a.getSalvageValue(), a.getDepreciableAmount(),
                a.getUsefulLifeYears(), a.getDepreciationMethod(), a.getDepreciationRatePct(),
                a.getAccumulatedDepreciation(), a.getCurrentBookValue(),
                a.getLastDepreciationDate(), a.getStatus(),
                a.getVendor() != null ? a.getVendor().getId() : null,
                a.getVendor() != null ? a.getVendor().getName() : null,
                a.getAssetAccount() != null ? a.getAssetAccount().getId() : null,
                a.getAssetAccount() != null ? a.getAssetAccount().getCode() : null,
                a.getAssetAccount() != null ? a.getAssetAccount().getName() : null,
                a.getDisposalDate(), a.getDisposalReason(), a.getDisposalProceeds(),
                a.getNotes(), a.getCreatedAt(), a.getCreatedBy());
    }

    public DepreciationEntryResponseDTO toDepDTO(DepreciationEntry d) {
        return new DepreciationEntryResponseDTO(d.getId(), d.getAsset().getId(),
                d.getAsset().getAssetCode(), d.getAsset().getName(),
                d.getFinancialYear(), d.getDepreciationDate(),
                d.getOpeningBookValue(), d.getDepreciationAmount(), d.getClosingBookValue(),
                d.getGlEntryId(), d.getNotes(), d.getCreatedAt(), d.getCreatedBy());
    }
}
