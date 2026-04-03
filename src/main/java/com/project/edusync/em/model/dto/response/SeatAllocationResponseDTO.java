package com.project.edusync.em.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Seat allocation result — returned after allocation and for allocation listing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatAllocationResponseDTO {

    private Long allocationId;
    private String studentName;
    private String enrollmentNumber;
    private String seatLabel;
    private String roomName;
    private int rowNumber;
    private int columnNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
