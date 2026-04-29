package com.project.edusync.finance.controller;

import com.project.edusync.finance.dto.invoice.InvoiceResponseDTO;
import com.project.edusync.finance.dto.payment.InitiatePaymentRequestDTO;
import com.project.edusync.finance.dto.payment.InitiatePaymentResponseDTO;
import com.project.edusync.finance.dto.payment.PaymentResponseDTO;
import com.project.edusync.finance.dto.payment.VerifyPaymentRequestDTO;
import com.project.edusync.finance.service.DashboardService;
import com.project.edusync.finance.service.InvoiceService;
import com.project.edusync.finance.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

/**
 * Controller for Parent-Facing financial endpoints.
 * Base path: /api/v1/finance/parent
 */
@RestController
@RequestMapping("${api.url}/auth/finance/parent")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PARENT', 'GUARDIAN')")
public class ParentFinanceController {

    private final InvoiceService invoiceService;
    private final DashboardService dashboardService;
    private final PaymentService paymentService;

    /**
     * GET /api/v1/finance/parent/invoices/for-student/{studentId}
     * Retrieves all invoices for a specific student.
     */
    @GetMapping("/invoices/for-student/{studentId}")
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoicesForStudent(
            @PathVariable Long studentId) {

        List<InvoiceResponseDTO> responseList = invoiceService.getInvoicesForStudent(studentId);
        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    /**
     * GET /api/v1/finance/parent/invoices/{invoiceId}
     * Gets the detailed line-item breakdown of a specific invoice.
     */
    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByIdForParent(
            @PathVariable Long invoiceId) {

        InvoiceResponseDTO response = invoiceService.getInvoiceByIdForParent(invoiceId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /api/v1/finance/parent/payments/initiate
     * Initiates an online payment for an invoice.
     * Body: { "invoiceId": 1, "amount": 20000.00 }
     */
    @PostMapping("/payments/initiate")
    public ResponseEntity<InitiatePaymentResponseDTO> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequestDTO requestDTO) throws Exception {

        InitiatePaymentResponseDTO response = paymentService.initiateOnlinePayment(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * POST /api/v1/finance/parent/payments/verify
     * Verifies the payment with Razorpay after the client-side popup succeeds.
     */
    @PostMapping("/payments/verify")
    public ResponseEntity<PaymentResponseDTO> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequestDTO verifyDTO) throws Exception {

        PaymentResponseDTO response = paymentService.verifyOnlinePayment(verifyDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * GET /api/v1/finance/parent/payments/for-student/{studentId}
     * Retrieves all successful payments made for a specific student.
     */
    @GetMapping("/payments/for-student/{studentId}")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsForStudent(
            @PathVariable Long studentId) {
        List<PaymentResponseDTO> payments = paymentService.getPaymentsForStudent(studentId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
}