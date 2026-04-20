package com.project.edusync.finance.dto.reminder;

import lombok.Data;

@Data
public class ReminderTemplateDTO {
    private Long id;
    private String name;
    private String subject;
    private String body;
    private String channel;
    private String triggerType;
    private Integer triggerDays;
    private Boolean isActive;
}
