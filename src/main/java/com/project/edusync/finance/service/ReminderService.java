package com.project.edusync.finance.service;

import com.project.edusync.finance.dto.reminder.ReminderLogDTO;
import com.project.edusync.finance.dto.reminder.ReminderTemplateCreateDTO;
import com.project.edusync.finance.dto.reminder.ReminderTemplateDTO;

import java.util.List;

public interface ReminderService {
    ReminderTemplateDTO createTemplate(ReminderTemplateCreateDTO dto);
    List<ReminderTemplateDTO> getAllTemplates();
    ReminderTemplateDTO toggleTemplate(Long id);
    
    void triggerBulkReminders();
    List<ReminderLogDTO> getAllLogs();
}
