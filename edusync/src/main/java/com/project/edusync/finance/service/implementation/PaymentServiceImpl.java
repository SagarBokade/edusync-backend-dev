package com.project.edusync.finance.service.implementation;

import com.project.edusync.common.exception.finance.InvalidPaymentOperationException;
import com.project.edusync.common.exception.finance.InvoiceNotFoundException;
import com.project.edusync.common.exception.finance.PaymentNotFoundException;
import com.project.edusync.common.exception.finance.StudentNotFoundException;
import com.project.edusync.finance.dto.payment.PaymentResponseDTO;
import com.project.edusync.finance.dto.payment.PaymentUpdateDTO;
import com.project.edusync.finance.dto.payment.RecordOfflinePaymentDTO;
import com.project.edusync.finance.mapper.PaymentMapper;
import com.project.edusync.finance.model.entity.Invoice;
import com.project.edusync.finance.model.entity.Payment;
import com.project.edusync.finance.model.enums.InvoiceStatus;
import com.project.edusync.finance.model.enums.PaymentStatus;
import com.project.edusync.finance.repository.InvoiceRepository;
import com.project.edusync.finance.repository.PaymentRepository;
import com.project.edusync.finance.service.PaymentService;
import com.project.edusync.uis.model.entity.Student;
import com.project.edusync.uis.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final StudentRepository studentRepository;
    private final PaymentMapper paymentMapper;
    private final ModelMapper modelMapper; // We keep it for other future methods

    @Override
    @Transactional
    public PaymentResponseDTO recordOfflinePayment(RecordOfflinePaymentDTO createDTO) {

        // 1. Find the related entities
        Invoice invoice = invoiceRepository.findById(createDTO.getInvoiceId())
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice not found with invoice ID: " + createDTO.getInvoiceId()));

        Student student = studentRepository.findById(createDTO.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException("Student not found with student ID: " + createDTO.getStudentId()));

        // 2. Perform Validation
        if (!invoice.getStudent().getId().equals(student.getId())) {
            throw new InvalidPaymentOperationException("Invoice studentId does not match provided studentId.");
        }
        if (invoice.getStatus() == InvoiceStatus.PAID || invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new InvalidPaymentOperationException("Invoice is already " + invoice.getStatus());
        }

        // --- FIX: Reverted to manual mapping for entity creation ---
        // 3. Create the new Payment entity manually
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setInvoice(invoice);
        payment.setAmountPaid(createDTO.getAmountPaid());
        payment.setPaymentMethod(createDTO.getPaymentMethod());
        payment.setTransactionId(createDTO.getTransactionId());
        payment.setNotes(createDTO.getNotes());
        payment.setStatus(PaymentStatus.SUCCESS); // Offline is considered an immediate success

        // Use provided payment date or set to now
        if (createDTO.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        } else {
            payment.setPaymentDate(createDTO.getPaymentDate());
        }
        // --- END OF FIX ---

        // 4. Update the Invoice
        BigDecimal newPaidAmount = invoice.getPaidAmount().add(createDTO.getAmountPaid());
        invoice.setPaidAmount(newPaidAmount);

        // Check if this payment makes the invoice fully paid
        // We use compareTo: 0 (equal), 1 (newPaidAmount is greater)
        if (newPaidAmount.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            // If it's a partial payment, just mark it as pending (or a new "PARTIAL" status)
            invoice.setStatus(InvoiceStatus.PENDING);
        }

        // 5. Save both entities in the transaction
        invoiceRepository.save(invoice);
        Payment savedPayment = paymentRepository.save(payment);

        // 6. Return the DTO for the new payment
        return paymentMapper.toDto(savedPayment);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDTO> getAllPayments(Pageable pageable) {
        Page<Payment> paymentPage = paymentRepository.findAll(pageable);
        return paymentPage.map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO updatePayment(Long paymentId, PaymentUpdateDTO updateDTO) {
        // 1. Find the existing payment
        Payment existingPayment = findPaymentById(paymentId);

        // 2. Use ModelMapper to apply the non-null updates from the DTO
        modelMapper.map(updateDTO, existingPayment);
        // Note: We might need to configure ModelMapper to skip nulls if
        // we want this to be a PATCH, but for a PUT this is fine.

        // 3. Save the updated entity
        Payment updatedPayment = paymentRepository.save(existingPayment);

        // 4. Return the response DTO
        return paymentMapper.toDto(updatedPayment);
    }

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with Payment ID: " + paymentId));
    }

}