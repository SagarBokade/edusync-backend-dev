package com.project.edusync.ams.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmsAttendanceTypeResponse {

    private Long typeId;
    private String typeName;
    private String shortCode;
    private boolean isPresentMark;
    private boolean isAbsenceMark;
    private String colorCode;
}