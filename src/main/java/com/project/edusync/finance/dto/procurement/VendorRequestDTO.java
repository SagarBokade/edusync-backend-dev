package com.project.edusync.finance.dto.procurement;

import com.project.edusync.finance.model.enums.VendorStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VendorRequestDTO(
    @NotBlank(message = "Vendor name is required") @Size(max = 200) String name,
    String legalType,
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
             message = "Invalid GSTIN format") String gstin,
    @Size(max = 10) String pan,
    String contactPerson,
    @Email String email,
    String phone,
    String address,
    String city,
    String state,
    String pincode,
    String bankAccountNumber,
    String bankName,
    String ifscCode,
    String category,
    Integer paymentTermsDays,
    VendorStatus status,
    String notes
) {}
