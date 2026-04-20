package com.project.edusync.finance.service;

import com.project.edusync.finance.dto.refund.RefundRecordDTO;
import com.project.edusync.finance.dto.refund.RefundRequestCreateDTO;

import java.util.List;

public interface RefundService {
    RefundRecordDTO requestRefund(RefundRequestCreateDTO dto);
    List<RefundRecordDTO> getAllRefunds();
    RefundRecordDTO updateRefundStatus(Long id, String status);
}
