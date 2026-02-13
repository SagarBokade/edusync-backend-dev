package com.project.edusync.adm.model.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * DTO for responding with available Teacher (ID and Name).
 * Uses Long for id.
 */
@Data
@Builder
public class AvailableTeacherDto {
    private Long id;
    private String name;
}