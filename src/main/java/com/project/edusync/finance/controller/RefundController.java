package com.project.edusync.finance.controller;

import com.project.edusync.finance.dto.refund.RefundRecordDTO;
import com.project.edusync.finance.dto.refund.RefundRequestCreateDTO;
import com.project.edusync.finance.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.url}/auth/finance/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    public ResponseEntity<RefundRecordDTO> requestRefund(@RequestBody @Valid RefundRequestCreateDTO dto) {
        return ResponseEntity.ok(refundService.requestRefund(dto));
    }

    @GetMapping
    public ResponseEntity<List<RefundRecordDTO>> getAllRefunds() {
        return ResponseEntity.ok(refundService.getAllRefunds());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RefundRecordDTO> updateRefundStatus(
            @PathVariable Long id, 
            @RequestParam String status) {
        return ResponseEntity.ok(refundService.updateRefundStatus(id, status));
    }
}
