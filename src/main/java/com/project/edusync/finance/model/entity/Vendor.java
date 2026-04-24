package com.project.edusync.finance.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.finance.model.enums.VendorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

/**
 * Represents a supplier/vendor in the Vendor Master.
 *
 * Vendors are shared across procurement modules:
 *   Vendor → PurchaseOrder → GoodsReceiptNote → VendorBill
 *
 * All vendors must be ACTIVE to receive new Purchase Orders.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "proc_vendors",
       uniqueConstraints = @UniqueConstraint(columnNames = {"gstin", "school_id"}))
public class Vendor extends AuditableEntity {

    @Column(name = "vendor_code", nullable = false, unique = true, length = 20)
    private String vendorCode;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** Legal type: Proprietorship, LLP, Pvt Ltd, Public Ltd, Trust, etc. */
    @Column(name = "legal_type", length = 100)
    private String legalType;

    @Column(name = "gstin", length = 15)
    private String gstin;

    @Column(name = "pan", length = 10)
    private String pan;

    @Column(name = "contact_person", length = 150)
    private String contactPerson;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    /** Bank account number for payment disbursement. */
    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_name", length = 150)
    private String bankName;

    @Column(name = "ifsc_code", length = 15)
    private String ifscCode;

    /** Category of services/goods the vendor provides (e.g., "IT Hardware", "Lab Chemicals"). */
    @Column(name = "category", length = 200)
    private String category;

    /** Default payment terms in days (e.g., 30 = Net 30). */
    @Column(name = "payment_terms_days")
    @ColumnDefault("30")
    private Integer paymentTermsDays = 30;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @ColumnDefault("'PENDING_VERIFICATION'")
    private VendorStatus status = VendorStatus.PENDING_VERIFICATION;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "school_id")
    private Long schoolId;
}
