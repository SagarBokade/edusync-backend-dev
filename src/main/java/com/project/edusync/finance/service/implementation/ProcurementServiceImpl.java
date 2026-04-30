package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.procurement.*;
import com.project.edusync.finance.model.entity.*;
import com.project.edusync.finance.model.enums.*;
import com.project.edusync.finance.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles Vendor Directory management, Purchase Order lifecycle,
 * and GRN creation with PO quantity updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProcurementServiceImpl {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository poRepository;
    private final GoodsReceiptNoteRepository grnRepository;
    private final BudgetRepository budgetRepository;

    private static final Long DEFAULT_SCHOOL_ID = 1L;

    // ══════════════════════════════════════════════════════════════════════════
    // VENDOR MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    public VendorResponseDTO createVendor(VendorRequestDTO dto, Long schoolId) {
        Vendor vendor = new Vendor();
        mapVendorDto(dto, vendor, schoolId);
        vendor.setVendorCode(generateVendorCode(schoolId));
        vendor.setStatus(dto.status() != null ? dto.status() : VendorStatus.PENDING_VERIFICATION);
        return toVendorDTO(vendorRepository.save(vendor));
    }

    public VendorResponseDTO updateVendor(Long id, VendorRequestDTO dto, Long schoolId) {
        Vendor vendor = findVendor(id, schoolId);
        mapVendorDto(dto, vendor, schoolId);
        if (dto.status() != null) vendor.setStatus(dto.status());
        return toVendorDTO(vendorRepository.save(vendor));
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getAllVendors(Long schoolId) {
        return vendorRepository.findBySchoolIdOrderByNameAsc(schoolId)
                .stream().map(this::toVendorDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getActiveVendors(Long schoolId) {
        return vendorRepository.findBySchoolIdAndStatusOrderByNameAsc(schoolId, VendorStatus.ACTIVE)
                .stream().map(this::toVendorDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VendorResponseDTO getVendorById(Long id, Long schoolId) {
        return toVendorDTO(findVendor(id, schoolId));
    }

    public void deactivateVendor(Long id, Long schoolId) {
        Vendor vendor = findVendor(id, schoolId);
        vendor.setStatus(VendorStatus.SUSPENDED);
        vendorRepository.save(vendor);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PURCHASE ORDER LIFECYCLE
    // ══════════════════════════════════════════════════════════════════════════

    public PurchaseOrderResponseDTO createPO(PurchaseOrderRequestDTO dto, Long schoolId) {
        Vendor vendor = findVendor(dto.vendorId(), schoolId);
        if (vendor.getStatus() != VendorStatus.ACTIVE) {
            throw new IllegalStateException("Vendor '" + vendor.getName() + "' must be ACTIVE to receive POs. Current: " + vendor.getStatus());
        }

        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(generatePONumber(schoolId));
        po.setVendor(vendor);
        po.setDepartment(dto.department());
        po.setOrderDate(dto.orderDate());
        po.setExpectedDeliveryDate(dto.expectedDeliveryDate());
        po.setDescription(dto.description());
        po.setNotes(dto.notes());
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setSchoolId(schoolId);

        if (dto.referenceBudgetId() != null) {
            budgetRepository.findById(dto.referenceBudgetId())
                    .ifPresent(po::setReferenceBudget);
        }

        PurchaseOrder saved = poRepository.save(po);

        // Add items
        int lineNum = 1;
        for (POItemRequestDTO itemDTO : dto.items()) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setDescription(itemDTO.description());
            item.setUnitOfMeasure(itemDTO.unitOfMeasure());
            item.setQuantity(itemDTO.quantity());
            item.setUnitPrice(itemDTO.unitPrice());
            item.setLineTotal(itemDTO.quantity().multiply(itemDTO.unitPrice()));
            item.setQuantityReceived(BigDecimal.ZERO);
            item.setLineNumber(lineNum++);
            saved.addItem(item);
        }

        BigDecimal gstPct = dto.gstPercentage() != null ? dto.gstPercentage() : BigDecimal.ZERO;
        saved.recalculateTotals(gstPct);
        return toPOResponseDTO(poRepository.save(saved));
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderResponseDTO> getAllPOs(Long schoolId) {
        return poRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId)
                .stream().map(this::toPOResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseOrderResponseDTO getPOById(Long id, Long schoolId) {
        return toPOResponseDTO(findPO(id, schoolId));
    }

    public PurchaseOrderResponseDTO submitPO(Long id, Long schoolId) {
        PurchaseOrder po = findPO(id, schoolId);
        if (po.getStatus() != PurchaseOrderStatus.DRAFT) throw new IllegalStateException("Only DRAFT POs can be submitted.");
        po.setStatus(PurchaseOrderStatus.SUBMITTED);
        return toPOResponseDTO(poRepository.save(po));
    }

    public PurchaseOrderResponseDTO approvePO(Long id, Long schoolId) {
        PurchaseOrder po = findPO(id, schoolId);
        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED) throw new IllegalStateException("Only SUBMITTED POs can be approved.");
        po.setStatus(PurchaseOrderStatus.APPROVED);
        po.setApprovedBy(getCurrentUsername());
        return toPOResponseDTO(poRepository.save(po));
    }

    public PurchaseOrderResponseDTO rejectPO(Long id, String reason, Long schoolId) {
        PurchaseOrder po = findPO(id, schoolId);
        if (po.getStatus() != PurchaseOrderStatus.SUBMITTED) throw new IllegalStateException("Only SUBMITTED POs can be rejected.");
        po.setStatus(PurchaseOrderStatus.REJECTED);
        po.setNotes((po.getNotes() != null ? po.getNotes() + "\n" : "") + "REJECTION REASON: " + reason);
        return toPOResponseDTO(poRepository.save(po));
    }

    public PurchaseOrderResponseDTO cancelPO(Long id, Long schoolId) {
        PurchaseOrder po = findPO(id, schoolId);
        if (po.getStatus() == PurchaseOrderStatus.FULLY_RECEIVED || po.getStatus() == PurchaseOrderStatus.CLOSED) {
            throw new IllegalStateException("Cannot cancel a PO in status: " + po.getStatus());
        }
        if (grnRepository.existsByPurchaseOrderId(id)) {
            throw new IllegalStateException("Cannot cancel a PO with existing GRN records. Reverse the GRN first.");
        }
        po.setStatus(PurchaseOrderStatus.CANCELLED);
        return toPOResponseDTO(poRepository.save(po));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GOODS RECEIPT NOTE (GRN)
    // ══════════════════════════════════════════════════════════════════════════

    public GRNResponseDTO createGRN(GRNRequestDTO dto, Long schoolId) {
        PurchaseOrder po = findPO(dto.purchaseOrderId(), schoolId);
        if (po.getStatus() != PurchaseOrderStatus.APPROVED &&
            po.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
            throw new IllegalStateException("GRN can only be created for APPROVED or PARTIALLY_RECEIVED POs.");
        }

        GoodsReceiptNote grn = new GoodsReceiptNote();
        grn.setGrnNumber(generateGRNNumber(schoolId));
        grn.setPurchaseOrder(po);
        grn.setReceiptDate(dto.receiptDate());
        grn.setReceivedBy(dto.receivedBy() != null ? dto.receivedBy() : getCurrentUsername());
        grn.setVendorChallanNumber(dto.vendorChallanNumber());
        grn.setNotes(dto.notes());
        grn.setStatus(GRNStatus.ACCEPTED);
        grn.setSchoolId(schoolId);

        GoodsReceiptNote savedGRN = grnRepository.save(grn);

        // Process items and update PO item quantities
        boolean allFullyReceived = true;
        int lineNum = 1;
        for (GRNItemRequestDTO itemDTO : dto.items()) {
            PurchaseOrderItem poItem = po.getItems().stream()
                    .filter(i -> i.getItemId().equals(itemDTO.poItemId()))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("PO item not found: " + itemDTO.poItemId()));

            GoodsReceiptItem grnItem = new GoodsReceiptItem();
            grnItem.setPurchaseOrderItem(poItem);
            grnItem.setDescription(itemDTO.description() != null ? itemDTO.description() : poItem.getDescription());
            grnItem.setReceivedQuantity(itemDTO.receivedQuantity());
            grnItem.setAcceptedQuantity(itemDTO.acceptedQuantity());
            grnItem.setRejectedQuantity(itemDTO.rejectedQuantity() != null ? itemDTO.rejectedQuantity() : itemDTO.receivedQuantity().subtract(itemDTO.acceptedQuantity()));
            grnItem.setRejectionReason(itemDTO.rejectionReason());
            grnItem.setLineNumber(lineNum++);
            savedGRN.addItem(grnItem);

            // Update PO item received quantity
            poItem.setQuantityReceived(poItem.getQuantityReceived().add(itemDTO.acceptedQuantity()));
            if (!poItem.isFullyReceived()) allFullyReceived = false;
        }
        grnRepository.save(savedGRN);

        // Update PO status
        po.setStatus(allFullyReceived ? PurchaseOrderStatus.FULLY_RECEIVED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
        poRepository.save(po);

        log.info("GRN {} created for PO {} (school {}). PO status → {}", savedGRN.getGrnNumber(), po.getPoNumber(), schoolId, po.getStatus());
        return toGRNResponseDTO(savedGRN);
    }

    @Transactional(readOnly = true)
    public List<GRNResponseDTO> getGRNsForPO(Long poId, Long schoolId) {
        findPO(poId, schoolId); // security check
        return grnRepository.findByPurchaseOrderIdOrderByReceiptDateDesc(poId)
                .stream().map(this::toGRNResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GRNResponseDTO getGRNById(Long id, Long schoolId) {
        GoodsReceiptNote grn = grnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("GRN not found: " + id));
        if (!schoolId.equals(grn.getSchoolId())) throw new EntityNotFoundException("GRN not found: " + id);
        return toGRNResponseDTO(grn);
    }

    @Transactional(readOnly = true)
    public List<GRNResponseDTO> getAllGRNs(Long schoolId) {
        return grnRepository.findBySchoolIdOrderByReceiptDateDesc(schoolId)
                .stream().map(this::toGRNResponseDTO).collect(Collectors.toList());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private Vendor findVendor(Long id, Long schoolId) {
        Vendor v = vendorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendor not found: " + id));
        if (!schoolId.equals(v.getSchoolId())) throw new EntityNotFoundException("Vendor not found: " + id);
        return v;
    }

    private PurchaseOrder findPO(Long id, Long schoolId) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found: " + id));
        if (!schoolId.equals(po.getSchoolId())) throw new EntityNotFoundException("PO not found: " + id);
        return po;
    }

    private void mapVendorDto(VendorRequestDTO dto, Vendor vendor, Long schoolId) {
        vendor.setName(dto.name()); vendor.setLegalType(dto.legalType()); vendor.setGstin(dto.gstin());
        vendor.setPan(dto.pan()); vendor.setContactPerson(dto.contactPerson()); vendor.setEmail(dto.email());
        vendor.setPhone(dto.phone()); vendor.setAddress(dto.address()); vendor.setCity(dto.city());
        vendor.setState(dto.state()); vendor.setPincode(dto.pincode());
        vendor.setBankAccountNumber(dto.bankAccountNumber()); vendor.setBankName(dto.bankName()); vendor.setIfscCode(dto.ifscCode());
        vendor.setCategory(dto.category()); vendor.setPaymentTermsDays(dto.paymentTermsDays() != null ? dto.paymentTermsDays() : 30);
        vendor.setNotes(dto.notes()); vendor.setSchoolId(schoolId);
    }

    private String generateVendorCode(Long schoolId) {
        long count = vendorRepository.countBySchoolId(schoolId);
        return "VND-" + String.format("%04d", count + 1);
    }

    private String generatePONumber(Long schoolId) {
        int year = Year.now().getValue();
        String prefix = "PO-" + year + "-";
        long count = poRepository.countByPrefix(schoolId, prefix);
        return prefix + String.format("%05d", count + 1);
    }

    private String generateGRNNumber(Long schoolId) {
        int year = Year.now().getValue();
        String prefix = "GRN-" + year + "-";
        long count = grnRepository.countByPrefix(schoolId, prefix);
        return prefix + String.format("%05d", count + 1);
    }

    private String getCurrentUsername() {
        try { return SecurityContextHolder.getContext().getAuthentication().getName(); } catch (Exception e) { return "system"; }
    }

    // ── Response Mappers ──────────────────────────────────────────────────────

    public VendorResponseDTO toVendorDTO(Vendor v) {
        return new VendorResponseDTO(v.getId(), v.getUuid(), v.getVendorCode(), v.getName(), v.getLegalType(),
                v.getGstin(), v.getPan(), v.getContactPerson(), v.getEmail(), v.getPhone(),
                v.getAddress(), v.getCity(), v.getState(), v.getPincode(),
                v.getBankAccountNumber(), v.getBankName(), v.getIfscCode(),
                v.getCategory(), v.getPaymentTermsDays(), v.getStatus(), v.getNotes(),
                v.getCreatedAt(), v.getCreatedBy());
    }

    public PurchaseOrderResponseDTO toPOResponseDTO(PurchaseOrder po) {
        List<POItemResponseDTO> items = po.getItems().stream()
                .sorted((a, b) -> { if (a.getLineNumber() == null || b.getLineNumber() == null) return 0; return a.getLineNumber().compareTo(b.getLineNumber()); })
                .map(i -> new POItemResponseDTO(i.getItemId(), i.getDescription(), i.getUnitOfMeasure(),
                        i.getQuantity(), i.getUnitPrice(), i.getLineTotal(),
                        i.getQuantityReceived(), i.getOutstandingQuantity(), i.isFullyReceived(), i.getLineNumber()))
                .collect(Collectors.toList());
        return new PurchaseOrderResponseDTO(po.getId(), po.getUuid(), po.getPoNumber(),
                po.getVendor().getId(), po.getVendor().getName(), po.getVendor().getVendorCode(),
                po.getDepartment(),
                po.getReferenceBudget() != null ? po.getReferenceBudget().getId() : null,
                po.getReferenceBudget() != null ? po.getReferenceBudget().getDepartmentName() + " " + po.getReferenceBudget().getAcademicYear() : null,
                po.getOrderDate(), po.getExpectedDeliveryDate(), po.getDescription(),
                po.getTotalBeforeTax(), po.getTaxAmount(), po.getTotalAmount(),
                po.getStatus(), po.getApprovedBy(), po.getNotes(), items,
                po.getGrns().size(), po.getCreatedAt(), po.getCreatedBy());
    }

    public GRNResponseDTO toGRNResponseDTO(GoodsReceiptNote grn) {
        List<GRNItemResponseDTO> items = grn.getItems().stream()
                .sorted((a, b) -> { if (a.getLineNumber() == null || b.getLineNumber() == null) return 0; return a.getLineNumber().compareTo(b.getLineNumber()); })
                .map(i -> new GRNItemResponseDTO(i.getGrnItemId(), i.getPurchaseOrderItem().getItemId(),
                        i.getDescription(), i.getReceivedQuantity(), i.getAcceptedQuantity(),
                        i.getRejectedQuantity(), i.getRejectionReason(), i.getLineNumber()))
                .collect(Collectors.toList());
        return new GRNResponseDTO(grn.getId(), grn.getUuid(), grn.getGrnNumber(),
                grn.getPurchaseOrder().getId(), grn.getPurchaseOrder().getPoNumber(),
                grn.getPurchaseOrder().getVendor().getId(), grn.getPurchaseOrder().getVendor().getName(),
                grn.getReceiptDate(), grn.getReceivedBy(), grn.getVendorChallanNumber(),
                grn.getStatus(), grn.getNotes(), items, grn.getCreatedAt(), grn.getCreatedBy());
    }
}
