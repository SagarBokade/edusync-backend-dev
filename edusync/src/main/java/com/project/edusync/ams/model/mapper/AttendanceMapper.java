//package com.project.edusync.ams.model.mapper;
//
//import com.project.edusync.ams.model.dto.response.AttendanceTypeResponseDTO;
//import com.project.edusync.ams.model.entity.AttendanceType;
//import com.project.edusync.ams.model.entity.StudentDailyAttendance;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Mapper for converting between Attendance entities and DTOs.
// */
//@Component
//public class AttendanceMapper {
//
//    /**
//     * Converts a Page of StudentDailyAttendance entities to a PaginatedResponseDTO of DTOs.
//     *
//     * @param page The JPA Page result.
//     * @param attendanceTypeMap Map of AttendanceType ID to Type entity for quick lookup.
//     * @param studentNameLookupMap Map of Student ID to Student Name (Placeholder for UIS Integration).
//     * @return The paginated response DTO.
//     */
//    public PaginatedResponseDTO<AttendanceTypeResponseDTO> toPaginatedDto( // <-- Uses local DTO reference
//                                                                           Page<StudentDailyAttendance> page,
//                                                                           java.util.Map<Long, AttendanceType> attendanceTypeMap,
//                                                                           java.util.Map<Long, String> studentNameLookupMap) {
//
//        List<AttendanceTypeResponseDTO> content = page.getContent().stream()
//                .map(entity -> toDto(entity, entity.getAttendanceType()))
//                .collect(Collectors.toList());
//
//        // Instantiating the correctly imported DTO
//        PaginatedResponseDTO<AttendanceTypeResponseDTO> dto = new PaginatedResponseDTO<>();
//        dto.setContent(content);
//        dto.setPage(page.getNumber());
//        dto.setSize(page.getSize());
//        dto.setTotalElements(page.getTotalElements());
//        dto.setTotalPages(page.getTotalPages());
//        // No more error: setIsLast() is now available
//        dto.setIsLast(page.isLast());
//
//        return dto;
//    }
//
//    /**
//     * Converts a single StudentDailyAttendance entity to its Response DTO.
//     *
//     * @param entity The StudentDailyAttendance entity.
//     * @param type The associated AttendanceType entity.
//     * @return The mapped AmsDailyAttendanceResponse DTO.
//     */
//    public AttendanceTypeResponseDTO toDto(StudentDailyAttendance entity, AttendanceType type) {
//        AttendanceTypeResponseDTO dto = new AttendanceTypeResponseDTO();
//        dto.setRecordId(entity.getRecordId());
//        dto.setStudentId(entity.getStudentId());
//
//        // Placeholder for UIS lookup
//        dto.setStudentName("Student " + entity.getStudentId() + " (External Lookup)");
//
//        dto.setAttendanceDate(entity.getAttendanceDate());
//        dto.setTypeName(type.getTypeName());
//        dto.setTypeShortCode(type.getShortCode());
//        dto.setAbsenceMark(type.isAbsenceMark());
//        dto.setTakenByStaffId(entity.getTakenByStaffId());
//        dto.setNotes(entity.getNotes());
//        dto.setCreatedAt(entity.getCreatedAt());
//        dto.setUpdatedAt(entity.getUpdatedAt());
//
//        return dto;
//    }
//}