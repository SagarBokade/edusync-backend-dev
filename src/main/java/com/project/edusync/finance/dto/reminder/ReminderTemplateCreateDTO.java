package com.project.edusync.finance.dto.reminder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReminderTemplateCreateDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String subject;
    @NotBlank
    private String body;
    @NotBlank
    private String channel;
    @NotBlank
    private String triggerType;
    @NotNull
    private Integer triggerDays;
}
