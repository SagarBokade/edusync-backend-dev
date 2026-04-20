package com.project.edusync.finance.mapper;

import com.project.edusync.finance.dto.refund.RefundRecordDTO;
import com.project.edusync.finance.model.entity.RefundRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface RefundMapper {
    @Mapping(target = "requestedAt", expression = "java(formatDate(record.getRequestedAt()))")
    @Mapping(target = "processedAt", expression = "java(formatDate(record.getProcessedAt()))")
    RefundRecordDTO toDto(RefundRecord record);

    default String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toLocalDate().toString();
    }
}
