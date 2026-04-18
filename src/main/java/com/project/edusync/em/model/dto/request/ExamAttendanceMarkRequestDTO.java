package com.project.edusync.em.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ExamAttendanceMarkRequestDTO {

    @NotNull
    private Long examScheduleId;

    @NotNull
    private Long roomId;

    @Valid
    @NotEmpty
    private List<ExamAttendanceMarkEntryDTO> entries;
}

