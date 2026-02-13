package com.project.edusync.ams.model.dto.response;

import lombok.Value;
import java.util.UUID;

/**
 * DTO used for server responses (GET, or after POST/PUT) to send AttendanceType details
 * back to the client. Includes read-only, system-managed fields.
 */
@Value
public class AttendanceTypeResponseDTO {

    /** Internal Primary Key */
    Long id;

    /** External Public Identifier */
    UUID uuid;

    String typeName;
    String shortCode;
    boolean isPresentMark;
    boolean isAbsenceMark;
    boolean isLateMark;
    String colorCode;
}