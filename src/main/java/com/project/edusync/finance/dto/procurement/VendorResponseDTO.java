package com.project.edusync.finance.dto.procurement;

import com.project.edusync.finance.model.enums.VendorStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record VendorResponseDTO(
    Long id, UUID uuid, String vendorCode, String name, String legalType,
    String gstin, String pan, String contactPerson, String email, String phone,
    String address, String city, String state, String pincode,
    String bankAccountNumber, String bankName, String ifscCode,
    String category, Integer paymentTermsDays, VendorStatus status,
    String notes, LocalDateTime createdAt, String createdBy
) {}
