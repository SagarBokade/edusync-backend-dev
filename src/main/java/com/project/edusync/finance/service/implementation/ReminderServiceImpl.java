package com.project.edusync.finance.service.implementation;

import com.project.edusync.finance.dto.reminder.ReminderLogDTO;
import com.project.edusync.finance.dto.reminder.ReminderTemplateCreateDTO;
import com.project.edusync.finance.dto.reminder.ReminderTemplateDTO;
import com.project.edusync.finance.mapper.ReminderMapper;
import com.project.edusync.finance.model.entity.ReminderLog;
import com.project.edusync.finance.model.entity.ReminderTemplate;
import com.project.edusync.finance.repository.ReminderLogRepository;
import com.project.edusync.finance.repository.ReminderTemplateRepository;
import com.project.edusync.finance.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderTemplateRepository templateRepository;
    private final ReminderLogRepository logRepository;
    private final ReminderMapper mapper;

    @Override
    @Transactional
    public ReminderTemplateDTO createTemplate(ReminderTemplateCreateDTO dto) {
        ReminderTemplate template = ReminderTemplate.builder()
                .name(dto.getName())
                .subject(dto.getSubject())
                .body(dto.getBody())
                .channel(dto.getChannel())
                .triggerType(dto.getTriggerType())
                .triggerDays(dto.getTriggerDays())
                .isActive(true)
                .build();
        return mapper.toDto(templateRepository.save(template));
    }

    @Override
    public List<ReminderTemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReminderTemplateDTO toggleTemplate(Long id) {
        ReminderTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        template.setIsActive(!template.getIsActive());
        return mapper.toDto(templateRepository.save(template));
    }

    @Override
    @Transactional
    public void triggerBulkReminders() {
        // Implement complex business logic here to scan for overdues
        // Since we are mocking the trigger execution for now according to the UI:
        ReminderLog mockLog1 = ReminderLog.builder()
                .studentId(2001L)
                .studentName("Bulk Test Student A")
                .templateName("3-Day Overdue Warning")
                .channel("EMAIL")
                .status("SENT")
                .invoiceNumber("INV-2024-0999")
                .amountDue(BigDecimal.valueOf(12000))
                .build();
        
        ReminderLog mockLog2 = ReminderLog.builder()
                .studentId(2002L)
                .studentName("Bulk Test Student B")
                .templateName("3-Day Overdue Warning")
                .channel("EMAIL")
                .status("SENT")
                .invoiceNumber("INV-2024-0998")
                .amountDue(BigDecimal.valueOf(8000))
                .build();

        logRepository.save(mockLog1);
        logRepository.save(mockLog2);
    }

    @Override
    public List<ReminderLogDTO> getAllLogs() {
        return logRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
