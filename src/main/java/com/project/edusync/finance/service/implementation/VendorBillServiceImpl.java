package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.procurement.VendorBillRequestDTO;
import com.project.edusync.finance.dto.procurement.VendorBillResponseDTO;
import com.project.edusync.finance.model.entity.*;
import com.project.edusync.finance.model.enums.JournalReferenceType;
import com.project.edusync.finance.model.enums.VendorBillStatus;
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
 * Vendor Bill (Accounts Payable) service with built-in 3-Way Match Engine.
 *
 * 3-Way Match Logic:
 *   PO total amount ≈ GRN accepted value ≈ Vendor Bill amount (within 2% tolerance)
 *   → If all three match: status = THREE_WAY_MATCHED
 *   → Otherwise:          status = MISMATCH (with explanation stored in matchResultNotes)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VendorBillServiceImpl {

    private final VendorBillRepository billRepository;
    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository poRepository;
    private final GoodsReceiptNoteRepository grnRepository;
    private final AccountRepository accountRepository;
    private final GeneralLedgerService glService;

    private static final Long DEFAULT_SCHOOL_ID = 1L;
    /** Tolerance for 3-way match: 2% variance is accepted. */
    private static final BigDecimal MATCH_TOLERANCE_PCT = BigDecimal.valueOf(2);

    // == Accounts Payable GL codes (seeded in COA) ==
    private static final String AP_ACCOUNT_CODE   = "2110"; // Accounts Payable (Credit on bill receipt)
    private static final String EXP_ACCOUNT_CODE  = "5410"; // Admin Expense (Debit on bill receipt)
    private static final String BANK_ACCOUNT_CODE = "1120"; // Bank (Credit on payment)

    // ── Create Bill & Auto-run 3-Way Match ────────────────────────────────────

    public VendorBillResponseDTO createBill(VendorBillRequestDTO dto, Long schoolId) {
        Vendor vendor = findVendor(dto.vendorId(), schoolId);

        VendorBill bill = new VendorBill();
        bill.setBillNumber(generateBillNumber(schoolId));
        bill.setVendorInvoiceNumber(dto.vendorInvoiceNumber());
        bill.setVendor(vendor);
        bill.setBillDate(dto.billDate());
        bill.setDueDate(dto.dueDate());
        bill.setBillAmount(dto.billAmount());
        BigDecimal tax = dto.taxAmount() != null ? dto.taxAmount() : BigDecimal.ZERO;
        bill.setTaxAmount(tax);
        bill.setTotalPayable(dto.billAmount().add(tax));
        bill.setNotes(dto.notes());
        bill.setStatus(VendorBillStatus.PENDING);
        bill.setSchoolId(schoolId);

        if (dto.purchaseOrderId() != null) {
            PurchaseOrder po = poRepository.findById(dto.purchaseOrderId())
                    .orElseThrow(() -> new EntityNotFoundException("PO not found: " + dto.purchaseOrderId()));
            bill.setPurchaseOrder(po);
        }

        if (dto.grnId() != null) {
            GoodsReceiptNote grn = grnRepository.findById(dto.grnId())
                    .orElseThrow(() -> new EntityNotFoundException("GRN not found: " + dto.grnId()));
            bill.setGoodsReceiptNote(grn);
        }

        VendorBill saved = billRepository.save(bill);

        // Auto-run 3-Way Match
        runThreeWayMatch(saved);
        saved = billRepository.save(saved);

        // Post GL entry for bill receipt: Dr Expense, Cr Accounts Payable
        postBillReceiptGLEntry(saved, schoolId);

        return toResponseDTO(saved);
    }

    // ── 3-Way Match Engine ────────────────────────────────────────────────────

    /**
     * Runs the 3-way match check on a bill.
     *
     * Checks:
     * 1. PO total ≈ Bill total (within 2%)
     * 2. GRN accepted total value ≈ Bill total (within 2%)
     *
     * Updates bill.status and bill.matchResultNotes.
     */
    public void runThreeWayMatch(VendorBill bill) {
        if (bill.getPurchaseOrder() == null) {
            // No PO linked — cannot 3-way match, leave as PENDING
            bill.setMatchResultNotes("No Purchase Order linked. Manual review required.");
            return;
        }

        PurchaseOrder po = bill.getPurchaseOrder();
        StringBuilder report = new StringBuilder();
        boolean passed = true;

        // Check 1: PO Amount vs Bill Amount
        BigDecimal poTotal = po.getTotalAmount();
        BigDecimal billTotal = bill.getTotalPayable();
        boolean poMatch = isWithinTolerance(poTotal, billTotal);
        report.append(String.format("PO Total: ₹%.2f | Bill Total: ₹%.2f → %s%n",
                poTotal, billTotal, poMatch ? "✓ MATCH" : "✗ MISMATCH"));
        if (!poMatch) passed = false;

        // Check 2: GRN Accepted Value vs Bill Amount
        if (bill.getGoodsReceiptNote() != null) {
            GoodsReceiptNote grn = bill.getGoodsReceiptNote();
            BigDecimal grnAcceptedValue = grn.getItems().stream()
                    .map(item -> item.getAcceptedQuantity()
                            .multiply(item.getPurchaseOrderItem().getUnitPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            boolean grnMatch = isWithinTolerance(grnAcceptedValue, billTotal);
            report.append(String.format("GRN Accepted Value: ₹%.2f | Bill Total: ₹%.2f → %s%n",
                    grnAcceptedValue, billTotal, grnMatch ? "✓ MATCH" : "✗ MISMATCH"));
            if (!grnMatch) passed = false;
        } else {
            report.append("GRN: Not linked — GRN check skipped.%n");
        }

        // Check 3: PO status must be FULLY_RECEIVED or PARTIALLY_RECEIVED
        boolean goodsReceived = po.getStatus() == com.project.edusync.finance.model.enums.PurchaseOrderStatus.FULLY_RECEIVED
                             || po.getStatus() == com.project.edusync.finance.model.enums.PurchaseOrderStatus.PARTIALLY_RECEIVED;
        report.append(String.format("PO Goods Receipt Status: %s → %s%n",
                po.getStatus(), goodsReceived ? "✓ RECEIVED" : "✗ NOT RECEIVED"));
        if (!goodsReceived) passed = false;

        bill.setMatchResultNotes(report.toString());
        bill.setMatchedBy(getCurrentUsername());

        if (passed) {
            bill.setStatus(VendorBillStatus.THREE_WAY_MATCHED);
            log.info("3-Way Match PASSED for Bill {}", bill.getBillNumber());
        } else {
            bill.setStatus(VendorBillStatus.MISMATCH);
            log.warn("3-Way Match FAILED for Bill {}: {}", bill.getBillNumber(), report);
        }
    }

    /** Approve a THREE_WAY_MATCHED bill for payment. */
    public VendorBillResponseDTO approveBillForPayment(Long billId, Long schoolId) {
        VendorBill bill = findBill(billId, schoolId);
        if (bill.getStatus() != VendorBillStatus.THREE_WAY_MATCHED) {
            throw new IllegalStateException("Only THREE_WAY_MATCHED bills can be approved for payment. Current: " + bill.getStatus());
        }
        bill.setStatus(VendorBillStatus.APPROVED_FOR_PAYMENT);
        return toResponseDTO(billRepository.save(bill));
    }

    /** Override MISMATCH status manually after Finance Admin investigation. */
    public VendorBillResponseDTO overrideMismatch(Long billId, String reason, Long schoolId) {
        VendorBill bill = findBill(billId, schoolId);
        if (bill.getStatus() != VendorBillStatus.MISMATCH) {
            throw new IllegalStateException("Only MISMATCH bills can be overridden.");
        }
        bill.setStatus(VendorBillStatus.THREE_WAY_MATCHED);
        bill.setMatchResultNotes((bill.getMatchResultNotes() != null ? bill.getMatchResultNotes() + "\n" : "")
                + "MANUAL OVERRIDE by " + getCurrentUsername() + ": " + reason);
        return toResponseDTO(billRepository.save(bill));
    }

    /** Mark a bill as PAID and post GL: Dr Accounts Payable, Cr Bank. */
    public VendorBillResponseDTO recordPayment(Long billId, String paymentReference, Long schoolId) {
        VendorBill bill = findBill(billId, schoolId);
        if (bill.getStatus() != VendorBillStatus.APPROVED_FOR_PAYMENT) {
            throw new IllegalStateException("Only APPROVED_FOR_PAYMENT bills can be marked paid. Current: " + bill.getStatus());
        }
        bill.setPaymentDate(LocalDate.now());
        bill.setPaymentReference(paymentReference);
        bill.setStatus(VendorBillStatus.PAID);

        // GL: Dr Accounts Payable (2110), Cr Bank (1120)
        try {
            var apOpt   = accountRepository.findByCodeAndSchoolId(AP_ACCOUNT_CODE, schoolId);
            var bankOpt = accountRepository.findByCodeAndSchoolId(BANK_ACCOUNT_CODE, schoolId);
            if (apOpt.isPresent() && bankOpt.isPresent()) {
                JournalEntry entry = glService.autoPostEntry(
                        LocalDate.now(),
                        "Vendor Payment — Bill " + bill.getBillNumber() + " | " + bill.getVendor().getName(),
                        JournalReferenceType.VENDOR_BILL,
                        bill.getId(),
                        apOpt.get().getId(),   // Debit AP (clears the liability)
                        bankOpt.get().getId(), // Credit Bank
                        bill.getTotalPayable(),
                        schoolId
                );
                bill.setGlEntryId(entry.getId());
            } else {
                log.warn("GL auto-post skipped for Bill #{}: COA not seeded.", bill.getBillNumber());
            }
        } catch (Exception ex) {
            log.error("GL post failed for Bill #{}: {}", bill.getBillNumber(), ex.getMessage());
        }

        return toResponseDTO(billRepository.save(bill));
    }

    public VendorBillResponseDTO cancelBill(Long billId, Long schoolId) {
        VendorBill bill = findBill(billId, schoolId);
        if (bill.getStatus() == VendorBillStatus.PAID) throw new IllegalStateException("Cannot cancel a PAID bill.");
        bill.setStatus(VendorBillStatus.CANCELLED);
        return toResponseDTO(billRepository.save(bill));
    }

    // ── Reads ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<VendorBillResponseDTO> getAllBills(Long schoolId) {
        return billRepository.findBySchoolIdOrderByBillDateDesc(schoolId)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendorBillResponseDTO> getBillsByStatus(VendorBillStatus status, Long schoolId) {
        return billRepository.findBySchoolIdAndStatusOrderByBillDateDesc(schoolId, status)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VendorBillResponseDTO getBillById(Long id, Long schoolId) {
        return toResponseDTO(findBill(id, schoolId));
    }

    @Transactional(readOnly = true)
    public BigDecimal getOutstandingPayables(Long schoolId) {
        return billRepository.sumOutstandingPayables(schoolId);
    }

    @Transactional(readOnly = true)
    public List<VendorBillResponseDTO> getOverdueBills(Long schoolId) {
        return billRepository.findOverdueBills(schoolId, LocalDate.now())
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private boolean isWithinTolerance(BigDecimal a, BigDecimal b) {
        if (a.compareTo(BigDecimal.ZERO) == 0 && b.compareTo(BigDecimal.ZERO) == 0) return true;
        if (a.compareTo(BigDecimal.ZERO) == 0) return false;
        BigDecimal diff = a.subtract(b).abs();
        BigDecimal toleranceAmt = a.multiply(MATCH_TOLERANCE_PCT).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return diff.compareTo(toleranceAmt) <= 0;
    }

    private void postBillReceiptGLEntry(VendorBill bill, Long schoolId) {
        try {
            var expOpt = accountRepository.findByCodeAndSchoolId(EXP_ACCOUNT_CODE, schoolId);
            var apOpt  = accountRepository.findByCodeAndSchoolId(AP_ACCOUNT_CODE, schoolId);
            if (expOpt.isEmpty() || apOpt.isEmpty()) {
                log.warn("GL: COA not seeded, skipping bill receipt entry for Bill #{}", bill.getBillNumber());
                return;
            }
            // Dr Expense, Cr Accounts Payable
            glService.autoPostEntry(
                    bill.getBillDate(),
                    "Vendor Bill Receipt — " + bill.getBillNumber() + " | " + bill.getVendor().getName(),
                    JournalReferenceType.VENDOR_BILL,
                    bill.getId() * -1L, // use negative ID to distinguish receipt from payment entry
                    expOpt.get().getId(),
                    apOpt.get().getId(),
                    bill.getTotalPayable(),
                    schoolId
            );
        } catch (Exception ex) {
            log.error("Bill receipt GL post failed: {}", ex.getMessage());
        }
    }

    private VendorBill findBill(Long id, Long schoolId) {
        VendorBill b = billRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor Bill not found: " + id));
        if (!schoolId.equals(b.getSchoolId())) throw new EntityNotFoundException("Vendor Bill not found: " + id);
        return b;
    }

    private Vendor findVendor(Long id, Long schoolId) {
        Vendor v = vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found: " + id));
        if (!schoolId.equals(v.getSchoolId())) throw new EntityNotFoundException("Vendor not found: " + id);
        return v;
    }

    private String generateBillNumber(Long schoolId) {
        int year = Year.now().getValue();
        String prefix = "BILL-" + year + "-";
        long count = billRepository.countByPrefix(schoolId, prefix);
        return prefix + String.format("%05d", count + 1);
    }

    private String getCurrentUsername() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); } catch (Exception e) { return "system"; }
    }

    public VendorBillResponseDTO toResponseDTO(VendorBill b) {
        boolean overdue = b.getDueDate() != null && LocalDate.now().isAfter(b.getDueDate())
                && b.getStatus() == VendorBillStatus.APPROVED_FOR_PAYMENT;
        return new VendorBillResponseDTO(b.getId(), b.getUuid(),
                b.getBillNumber(), b.getVendorInvoiceNumber(),
                b.getVendor().getId(), b.getVendor().getName(), b.getVendor().getVendorCode(),
                b.getPurchaseOrder() != null ? b.getPurchaseOrder().getId() : null,
                b.getPurchaseOrder() != null ? b.getPurchaseOrder().getPoNumber() : null,
                b.getGoodsReceiptNote() != null ? b.getGoodsReceiptNote().getId() : null,
                b.getGoodsReceiptNote() != null ? b.getGoodsReceiptNote().getGrnNumber() : null,
                b.getBillDate(), b.getDueDate(),
                b.getBillAmount(), b.getTaxAmount(), b.getTotalPayable(),
                b.getStatus(), b.getMatchResultNotes(), b.getMatchedBy(),
                b.getPaymentDate(), b.getPaymentReference(), b.getGlEntryId(),
                b.getNotes(), overdue, b.getCreatedAt(), b.getCreatedBy());
    }
}
