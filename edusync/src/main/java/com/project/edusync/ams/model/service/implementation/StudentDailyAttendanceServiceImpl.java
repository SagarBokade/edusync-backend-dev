//package com.project.edusync.ams.model.service.implementation;
//
//import com.project.edusync.ams.model.mapper.AttendanceMapper;
//import com.project.edusync.ams.model.dto.request.StudentAttendanceRequestDTO;
//import com.project.edusync.ams.model.dto.response.AttendanceTypeResponseDTO;
//import com.project.edusync.ams.model.entity.AttendanceType;
//import com.project.edusync.ams.model.entity.StudentDailyAttendance;
//import com.project.edusync.ams.model.exception.AttendanceProcessingException;
//import com.project.edusync.ams.model.exception.AttendanceRecordNotFoundException;
//import com.project.edusync.ams.model.exception.InvalidAttendanceTypeException;
//import com.project.edusync.ams.model.repository.AttendanceTypeRepository;
//import com.project.edusync.ams.model.repository.StudentDailyAttendanceRepository;
//import com.project.edusync.ams.model.service.StudentDailyAttendanceService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class StudentDailyAttendanceServiceImpl implements StudentDailyAttendanceService {
//
//    private final StudentDailyAttendanceRepository studentDailyAttendanceRepository;
//    private final AttendanceTypeRepository attendanceTypeRepository;
//    private final AttendanceMapper attendanceMapper;
//
//    // Defined set of short codes that require notes (Non-Present marks)
//    private static final Set<String> SHORT_CODES_REQUIRING_NOTES = Set.of("A", "UA", "L", "E", "S"); // Absent, Unexcused, Late Arrival, Excused, Suspended
//
//    @Override
//    @Transactional(rollbackFor = AttendanceProcessingException.class)
//    public List<AttendanceTypeResponseDTO> markAttendanceBatch(List<StudentAttendanceRequestDTO> requests, Long authenticatedStaffId) {
//        log.info("Starting batch attendance marking for {} requests by staff ID: {}", requests.size(), authenticatedStaffId);
//
//        // --- Edge Case / Null Handling ---
//        if (requests == null || requests.isEmpty()) {
//            log.warn("Received empty or null list for batch attendance marking.");
//            return List.of();
//        }
//        if (authenticatedStaffId == null) {
//            log.error("Authenticated Staff ID is NULL. Cannot proceed with audit-sensitive operation.");
//            throw new AttendanceProcessingException("Authenticated Staff ID is required for marking attendance.");
//        }
//
//        // 1. Collect all unique short codes from the batch requests
//        Set<String> uniqueShortCodes = requests.stream()
//                .map(StudentAttendanceRequestDTO::getTypeShortCode)
//                .filter(Objects::nonNull)
//                .map(String::toUpperCase)
//                .collect(Collectors.toSet());
//
//        // 2. Pre-fetch all required AttendanceType entities by their short code
//        Map<String, AttendanceType> typeMap = attendanceTypeRepository.findAll().stream()
//                .collect(Collectors.toMap(AttendanceType::getShortCode, type -> type));
//
//        if (!typeMap.keySet().containsAll(uniqueShortCodes)) {
//            // Find the missing codes for better error reporting
//            uniqueShortCodes.removeAll(typeMap.keySet());
//            log.error("Invalid Attendance Type short code(s) submitted: {}", uniqueShortCodes);
//            throw new InvalidAttendanceTypeException("One or more submitted Attendance Type short codes are invalid: " + uniqueShortCodes);
//        }
//
//        List<StudentDailyAttendance> entitiesToSave = new ArrayList<>();
//        List<AttendanceTypeResponseDTO> successfulResponses = new ArrayList<>();
//
//        for (StudentAttendanceRequestDTO request : requests) {
//            try {
//                // Null/Edge case validation for each individual record
//                if (request.getStudentId() == null || request.getAttendanceDate() == null || request.getTypeShortCode() == null) {
//                    log.warn("Skipping request due to null required field: StudentId={}, Date={}, Code={}", request.getStudentId(), request.getAttendanceDate(), request.getTypeShortCode());
//                    continue;
//                }
//
//                String shortCode = request.getTypeShortCode().toUpperCase();
//                AttendanceType type = typeMap.get(shortCode);
//
//                // Business Rule: Required Notes for Non-Present Marks
//                if (SHORT_CODES_REQUIRING_NOTES.contains(shortCode) && (request.getNotes() == null || request.getNotes().trim().isEmpty())) {
//                    log.error("Notes are required for attendance type: {}", type.getTypeName());
//                    throw new AttendanceProcessingException("Notes are mandatory for the attendance type: " + type.getTypeName());
//                }
//
//                // Business Rule: Prevent duplicate records for the same student/date
//                if (studentDailyAttendanceRepository.findByStudentIdAndAttendanceDate(request.getStudentId(), request.getAttendanceDate()).isPresent()) {
//                    log.warn("Duplicate record found and skipped for Student ID {} on {}", request.getStudentId(), request.getAttendanceDate());
//                    continue;
//                }
//
//                // 3. Mapping and Auditing
//                StudentDailyAttendance entity = new StudentDailyAttendance();
//                entity.setStudentId(request.getStudentId());
//                entity.setAttendanceDate(request.getAttendanceDate());
//                entity.setAttendanceType(type);
//                entity.setTakenByStaffId(authenticatedStaffId); // Use authenticated ID for audit
//                entity.setNotes(request.getNotes());
//
//                entitiesToSave.add(entity);
//
//            } catch (AttendanceProcessingException e) {
//                log.error("Validation failed for record (Student ID: {}): {}", request.getStudentId(), e.getMessage());
//                // In a production scenario, you might collect and report individual errors here
//                // For now, we continue processing the batch
//            }
//        }
//
//        // 4. Batch Save
//        if (!entitiesToSave.isEmpty()) {
//            List<StudentDailyAttendance> savedEntities = studentDailyAttendanceRepository.saveAll(entitiesToSave);
//            successfulResponses = savedEntities.stream()
//                    .map(entity -> attendanceMapper.toDto(entity, entity.getAttendanceType()))
//                    .collect(Collectors.toList());
//        }
//
//        log.info("Batch attendance marking completed. {} records successfully created/processed.", successfulResponses.size());
//        return successfulResponses;
//    }
//
////    @Override
////    @Transactional(readOnly = true)
////    public PaginatedResponseDTO<AmsDailyAttendanceResponse> getFilteredAttendanceRecords(AttendanceRecordFilterDTO filterDTO, Pageable pageable) {
////        log.info("Retrieving filtered attendance records. Filters: {}, Page: {}", filterDTO, pageable.getPageNumber());
////
////        // --- Edge Case / Null Handling ---
////        if (filterDTO == null) {
////            filterDTO = new AttendanceRecordFilterDTO();
////        }
////
////        Page<StudentDailyAttendance> page = studentDailyAttendanceRepository.findFiltered(
////                filterDTO.getStudentId(),
////                filterDTO.getStaffId(),
////                filterDTO.getStartDate(),
////                filterDTO.getEndDate(),
////                pageable);
////
////        // Pre-fetch all attendance types for efficient mapping (optional, but good practice)
////        Map<Long, AttendanceType> typeMap = attendanceTypeRepository.findAll().stream()
////                .collect(Collectors.toMap(AttendanceType::getTypeId, type -> type));
////
////        log.info("Finished retrieving attendance records. Total elements: {}", page.getTotalElements());
////        return attendanceMapper.toPaginatedDto(page, typeMap, Map.of()); // Empty student map placeholder
////    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public AttendanceTypeResponseDTO getAttendanceRecordById(Long recordId) {
//        log.info("Attempting to retrieve attendance record by ID: {}", recordId);
//
//        // --- Edge Case / Null Handling ---
//        if (recordId == null) {
//            throw new AttendanceProcessingException("Record ID cannot be null.");
//        }
//
//        StudentDailyAttendance entity = studentDailyAttendanceRepository.findByRecordIdAndIsDeletedFalse(recordId)
//                .orElseThrow(() -> new AttendanceRecordNotFoundException("Attendance record not found with ID: " + recordId));
//
//        log.info("Successfully retrieved record ID: {}", recordId);
//        return attendanceMapper.toDto(entity, entity.getAttendanceType());
//    }
//
//    @Override
//    @Transactional
//    public AttendanceTypeResponseDTO updateAttendanceRecord(Long recordId, StudentAttendanceRequestDTO request, Long authenticatedStaffId) {
//        log.info("Starting update for attendance record ID: {} by staff ID: {}", recordId, authenticatedStaffId);
//
//        // --- Edge Case / Null Handling ---
//        if (recordId == null || authenticatedStaffId == null) {
//            throw new AttendanceProcessingException("Record ID and authenticated Staff ID cannot be null for update.");
//        }
//
//        StudentDailyAttendance entity = studentDailyAttendanceRepository.findByRecordIdAndIsDeletedFalse(recordId)
//                .orElseThrow(() -> new AttendanceRecordNotFoundException("Attendance record not found with ID: " + recordId));
//
//        // 1. Find new AttendanceType by Short Code
//        String newShortCode = Optional.ofNullable(request.getTypeShortCode()).orElseThrow(() -> new AttendanceProcessingException("New Attendance Type short code is required for update.")).toUpperCase();
//
//        AttendanceType newType = attendanceTypeRepository.findByShortCode(newShortCode)
//                .orElseThrow(() -> new InvalidAttendanceTypeException("Invalid Attendance Type short code submitted: " + newShortCode));
//
//        // 2. Apply Business Rule: Required Notes
//        if (SHORT_CODES_REQUIRING_NOTES.contains(newShortCode) && (request.getNotes() == null || request.getNotes().trim().isEmpty())) {
//            log.error("Notes are required for attendance type: {}", newType.getTypeName());
//            throw new AttendanceProcessingException("Notes are mandatory for the attendance type: " + newType.getTypeName());
//        }
//
//        // 3. Update fields (preserving original creation info)
//        entity.setAttendanceType(newType);
//        entity.setTakenByStaffId(authenticatedStaffId); // Update audit field to the current modifier
//        entity.setNotes(request.getNotes());
//
//        // Note: studentId and attendanceDate are typically immutable after creation.
//
//        StudentDailyAttendance updatedEntity = studentDailyAttendanceRepository.save(entity);
//
//        log.info("Successfully updated attendance record ID: {}", recordId);
//        return attendanceMapper.toDto(updatedEntity, updatedEntity.getAttendanceType());
//    }
//
////    @Override
////    @Transactional
////    public void deleteAttendanceRecord(Long recordId, Long authenticatedStaffId) {
////        log.warn("Starting soft-delete for attendance record ID: {} by staff ID: {}", recordId, authenticatedStaffId);
////
////        // --- Edge Case / Null Handling ---
////        if (recordId == null || authenticatedStaffId == null) {
////            throw new AttendanceProcessingException("Record ID and authenticated Staff ID cannot be null for deletion.");
////        }
////
////        StudentDailyAttendance entity = studentDailyAttendanceRepository.findByRecordIdAndIsDeletedFalse(recordId)
////                .orElseThrow(() -> new AttendanceRecordNotFoundException("Attendance record not found with ID: " + recordId));
////
////        // Soft Delete implementation
////        entity.setDeleted(true);
////        // Set update audit fields (assuming they are handled by AuditableEntity)
////        // entity.setUpdatedBy(authenticatedStaffId);
////
////        studentDailyAttendanceRepository.save(entity);
////        log.warn("Successfully soft-deleted attendance record ID: {}. Marked as deleted in database.", recordId);
////    }
//}