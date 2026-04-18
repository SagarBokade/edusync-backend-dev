package com.project.edusync.em.model.service;

import com.project.edusync.adm.model.entity.Room;
import com.project.edusync.adm.repository.RoomRepository;
import com.project.edusync.common.exception.BadRequestException;
import com.project.edusync.common.exception.ResourceNotFoundException;
import com.project.edusync.common.security.AuthUtil;
import com.project.edusync.em.model.dto.request.ExamAttendanceFinalizeRequestDTO;
import com.project.edusync.em.model.dto.request.ExamAttendanceMarkEntryDTO;
import com.project.edusync.em.model.dto.request.ExamAttendanceMarkRequestDTO;
import com.project.edusync.em.model.dto.response.ExamAttendanceFinalizeResponseDTO;
import com.project.edusync.em.model.dto.response.ExamAttendanceMarkResponseDTO;
import com.project.edusync.em.model.dto.response.ExamRoomStudentResponseDTO;
import com.project.edusync.em.model.dto.response.InvigilatorRoomResponseDTO;
import com.project.edusync.em.model.entity.ExamAttendance;
import com.project.edusync.em.model.entity.ExamSchedule;
import com.project.edusync.em.model.enums.ExamAttendanceStatus;
import com.project.edusync.em.model.repository.ExamAttendanceRepository;
import com.project.edusync.em.model.repository.ExamScheduleRepository;
import com.project.edusync.em.model.repository.InvigilationRepository;
import com.project.edusync.em.model.repository.SeatAllocationRepository;
import com.project.edusync.uis.model.entity.Staff;
import com.project.edusync.uis.model.entity.Student;
import com.project.edusync.uis.repository.StaffRepository;
import com.project.edusync.uis.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamAttendanceService {

    private final AuthUtil authUtil;
    private final StaffRepository staffRepository;
    private final StudentRepository studentRepository;
    private final InvigilationRepository invigilationRepository;
    private final SeatAllocationRepository seatAllocationRepository;
    private final ExamAttendanceRepository examAttendanceRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final RoomRepository roomRepository;

    public List<InvigilatorRoomResponseDTO> getAssignedRoomsForCurrentInvigilator() {
        Long staffId = resolveCurrentStaffId();
        return invigilationRepository.findAssignedRoomsByStaffId(staffId)
            .stream()
            .map(r -> InvigilatorRoomResponseDTO.builder()
                .examScheduleId(r.getExamScheduleId())
                .roomId(r.getRoomId())
                .roomName(r.getRoomName())
                .subjectName(r.getSubjectName())
                .className(r.getClassName())
                .examDate(r.getExamDate())
                .startTime(r.getStartTime())
                .endTime(r.getEndTime())
                .build())
            .toList();
    }

    public List<ExamRoomStudentResponseDTO> getRoomAttendanceRoster(Long roomId, Long examScheduleId) {
        Long staffId = resolveCurrentStaffId();
        validateInvigilatorAssignment(staffId, examScheduleId, roomId);

        Map<Long, ExamAttendance> existingAttendance = examAttendanceRepository
            .findByExamScheduleIdAndRoomIdWithStudent(examScheduleId, roomId)
            .stream()
            .collect(Collectors.toMap(e -> e.getStudent().getId(), Function.identity(), (a, b) -> a));

        return seatAllocationRepository.findExamRoomStudents(examScheduleId, roomId)
            .stream()
            .map(s -> {
                ExamAttendance existing = existingAttendance.get(s.getStudentId());
                return ExamRoomStudentResponseDTO.builder()
                    .studentId(s.getStudentId())
                    .rollNo(s.getRollNo())
                    .name(buildName(s.getFirstName(), s.getLastName()))
                    .className(s.getClassName())
                    .seatPosition(toSeatPositionLabel(s.getPositionIndex()))
                    .seatLabel(s.getSeatLabel())
                    .status(existing == null ? null : existing.getStatus())
                    .finalized(existing != null && existing.isFinalized())
                    .build();
            })
            .toList();
    }

    @Transactional
    public ExamAttendanceMarkResponseDTO markAttendance(ExamAttendanceMarkRequestDTO request) {
        Long staffId = resolveCurrentStaffId();
        validateInvigilatorAssignment(staffId, request.getExamScheduleId(), request.getRoomId());
        ensureNotFinalized(request.getExamScheduleId(), request.getRoomId());

        Set<Long> roomStudentIds = new HashSet<>(seatAllocationRepository.findExamRoomStudentIds(request.getExamScheduleId(), request.getRoomId()));
        if (roomStudentIds.isEmpty()) {
            throw new BadRequestException("No seat allocations found for this room and exam schedule");
        }

        Set<Long> requestedStudentIds = request.getEntries().stream().map(ExamAttendanceMarkEntryDTO::getStudentId).collect(Collectors.toSet());
        if (requestedStudentIds.size() != request.getEntries().size()) {
            throw new BadRequestException("Duplicate student entries are not allowed in one mark request");
        }
        if (!roomStudentIds.containsAll(requestedStudentIds)) {
            throw new BadRequestException("One or more students are not allocated in this exam room");
        }

        Map<Long, ExamAttendance> existingByStudentId = examAttendanceRepository
            .findByExamScheduleIdAndStudentIds(request.getExamScheduleId(), requestedStudentIds)
            .stream()
            .collect(Collectors.toMap(e -> e.getStudent().getId(), Function.identity(), (a, b) -> a));

        ExamSchedule schedule = examScheduleRepository.findById(request.getExamScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("ExamSchedule", "id", request.getExamScheduleId()));
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));
        Staff marker = staffRepository.findById(staffId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));

        Set<Long> missingStudents = new HashSet<>(requestedStudentIds);
        missingStudents.removeAll(existingByStudentId.keySet());
        Map<Long, Student> missingStudentEntities = missingStudents.isEmpty()
            ? Collections.emptyMap()
            : studentRepository.findAllById(missingStudents).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));

        List<ExamAttendance> toSave = new ArrayList<>();
        for (ExamAttendanceMarkEntryDTO entry : request.getEntries()) {
            ExamAttendance attendance = existingByStudentId.get(entry.getStudentId());
            if (attendance == null) {
                Student student = missingStudentEntities.get(entry.getStudentId());
                if (student == null) {
                    throw new ResourceNotFoundException("Student", "id", entry.getStudentId());
                }
                attendance = ExamAttendance.builder()
                    .examSchedule(schedule)
                    .student(student)
                    .room(room)
                    .build();
            }
            if (attendance.isFinalized()) {
                throw new BadRequestException("Attendance is already finalized for this room");
            }
            attendance.setStatus(entry.getStatus());
            attendance.setMarkedBy(marker);
            attendance.setTimestamp(LocalDateTime.now());
            attendance.setFinalized(false);
            toSave.add(attendance);
        }

        examAttendanceRepository.saveAll(toSave);

        return ExamAttendanceMarkResponseDTO.builder()
            .savedCount(toSave.size())
            .finalized(false)
            .build();
    }

    @Transactional
    public ExamAttendanceFinalizeResponseDTO finalizeAttendance(ExamAttendanceFinalizeRequestDTO request) {
        Long staffId = resolveCurrentStaffId();
        validateInvigilatorAssignment(staffId, request.getExamScheduleId(), request.getRoomId());
        ensureNotFinalized(request.getExamScheduleId(), request.getRoomId());

        ExamSchedule schedule = examScheduleRepository.findByIdWithTimeslot(request.getExamScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("ExamSchedule", "id", request.getExamScheduleId()));

        LocalDateTime examEndTime = LocalDateTime.of(schedule.getExamDate(), schedule.getTimeslot().getEndTime());
        if (LocalDateTime.now().isBefore(examEndTime)) {
            throw new BadRequestException("Attendance can be finalized only after exam end time");
        }

        List<Long> roomStudentIds = seatAllocationRepository.findExamRoomStudentIds(request.getExamScheduleId(), request.getRoomId());
        if (roomStudentIds.isEmpty()) {
            throw new BadRequestException("No seat allocations found for this room and exam schedule");
        }

        Map<Long, ExamAttendance> existingByStudentId = examAttendanceRepository
            .findByExamScheduleIdAndStudentIds(request.getExamScheduleId(), roomStudentIds)
            .stream()
            .collect(Collectors.toMap(e -> e.getStudent().getId(), Function.identity(), (a, b) -> a));

        Staff marker = staffRepository.findById(staffId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", staffId));
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        List<ExamAttendance> toSave = new ArrayList<>(roomStudentIds.size());
        int alreadyMarked = 0;
        int autoMarkedAbsent = 0;

        Set<Long> missingStudentIds = new HashSet<>(roomStudentIds);
        missingStudentIds.removeAll(existingByStudentId.keySet());
        Map<Long, Student> missingStudents = missingStudentIds.isEmpty()
            ? Collections.emptyMap()
            : studentRepository.findAllById(missingStudentIds).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));

        for (Long studentId : roomStudentIds) {
            ExamAttendance attendance = existingByStudentId.get(studentId);
            if (attendance == null) {
                Student student = missingStudents.get(studentId);
                if (student == null) {
                    throw new ResourceNotFoundException("Student", "id", studentId);
                }
                attendance = ExamAttendance.builder()
                    .examSchedule(schedule)
                    .student(student)
                    .room(room)
                    .status(ExamAttendanceStatus.ABSENT)
                    .markedBy(marker)
                    .timestamp(LocalDateTime.now())
                    .finalized(true)
                    .build();
                autoMarkedAbsent++;
            } else {
                alreadyMarked++;
                attendance.setFinalized(true);
            }
            toSave.add(attendance);
        }

        examAttendanceRepository.saveAll(toSave);

        return ExamAttendanceFinalizeResponseDTO.builder()
            .totalStudents(roomStudentIds.size())
            .alreadyMarked(alreadyMarked)
            .autoMarkedAbsent(autoMarkedAbsent)
            .finalized(true)
            .build();
    }

    private Long resolveCurrentStaffId() {
        Long userId = authUtil.getCurrentUserId();
        return staffRepository.findByUserProfile_User_Id(userId)
            .map(Staff::getId)
            .orElseThrow(() -> new BadRequestException("Logged-in user is not mapped to staff"));
    }

    private void validateInvigilatorAssignment(Long staffId, Long examScheduleId, Long roomId) {
        if (!invigilationRepository.existsByExamScheduleIdAndRoom_IdAndStaffId(examScheduleId, roomId, staffId)) {
            throw new BadRequestException("You are not assigned as invigilator for this room and exam");
        }
    }

    private void ensureNotFinalized(Long examScheduleId, Long roomId) {
        if (examAttendanceRepository.existsByExamScheduleIdAndRoomIdAndFinalizedTrue(examScheduleId, roomId)) {
            throw new BadRequestException("Attendance already finalized for this room");
        }
    }

    private String buildName(String firstName, String lastName) {
        String left = firstName == null ? "" : firstName.trim();
        String right = lastName == null ? "" : lastName.trim();
        return (left + " " + right).trim();
    }

    private String toSeatPositionLabel(Integer positionIndex) {
        if (positionIndex == null) {
            return "";
        }
        return switch (positionIndex) {
            case 0 -> "LEFT";
            case 1 -> "MIDDLE";
            case 2 -> "RIGHT";
            default -> "POSITION-" + positionIndex;
        };
    }
}

