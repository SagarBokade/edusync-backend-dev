package com.project.edusync.finance.controller;

import com.project.edusync.finance.dto.reminder.ReminderLogDTO;
import com.project.edusync.finance.dto.reminder.ReminderTemplateCreateDTO;
import com.project.edusync.finance.dto.reminder.ReminderTemplateDTO;
import com.project.edusync.finance.service.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.url}/auth/finance/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping("/templates")
    public ResponseEntity<ReminderTemplateDTO> createTemplate(@RequestBody @Valid ReminderTemplateCreateDTO dto) {
        return ResponseEntity.ok(reminderService.createTemplate(dto));
    }

    @GetMapping("/templates")
    public ResponseEntity<List<ReminderTemplateDTO>> getAllTemplates() {
        return ResponseEntity.ok(reminderService.getAllTemplates());
    }

    @PutMapping("/templates/{id}/toggle")
    public ResponseEntity<ReminderTemplateDTO> toggleTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(reminderService.toggleTemplate(id));
    }

    @PostMapping("/trigger-bulk")
    public ResponseEntity<Void> triggerBulkReminders() {
        reminderService.triggerBulkReminders();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<List<ReminderLogDTO>> getAllLogs() {
        return ResponseEntity.ok(reminderService.getAllLogs());
    }
}
