package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.refund.RefundRecordDTO;
import com.project.edusync.finance.dto.refund.RefundRequestCreateDTO;
import com.project.edusync.finance.mapper.RefundMapper;
import com.project.edusync.finance.model.entity.RefundRecord;
import com.project.edusync.finance.repository.RefundRepository;
import com.project.edusync.finance.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final RefundRepository refundRepository;
    private final RefundMapper mapper;

    @Override
    @Transactional
    public RefundRecordDTO requestRefund(RefundRequestCreateDTO dto) {
        RefundRecord record = RefundRecord.builder()
                .studentId(dto.getStudentId())
                .studentName(dto.getStudentName())
                .paymentId(dto.getPaymentId())
                .invoiceNumber(dto.getInvoiceNumber())
                .refundAmount(dto.getRefundAmount())
                .reason(dto.getReason())
                .refundMethod(dto.getRefundMethod())
                .status("REQUESTED")
                .build();
        return mapper.toDto(refundRepository.save(record));
    }

    @Override
    public List<RefundRecordDTO> getAllRefunds() {
        return refundRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RefundRecordDTO updateRefundStatus(Long id, String status) {
        RefundRecord record = refundRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refund request not found"));

        record.setStatus(status);
        if ("PROCESSED".equals(status)) {
            record.setProcessedAt(LocalDateTime.now());
        }
        return mapper.toDto(refundRepository.save(record));
    }
}
