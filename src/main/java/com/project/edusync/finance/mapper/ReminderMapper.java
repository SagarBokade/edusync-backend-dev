package com.project.edusync.finance.mapper;

import com.project.edusync.finance.dto.reminder.ReminderLogDTO;
import com.project.edusync.finance.dto.reminder.ReminderTemplateDTO;
import com.project.edusync.finance.model.entity.ReminderLog;
import com.project.edusync.finance.model.entity.ReminderTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface ReminderMapper {
    ReminderTemplateDTO toDto(ReminderTemplate template);

    @Mapping(target = "sentAt", expression = "java(formatDate(log.getSentAt()))")
    ReminderLogDTO toDto(ReminderLog log);

    default String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
