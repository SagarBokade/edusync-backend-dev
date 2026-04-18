package com.project.edusync.em.model.service;

import com.project.edusync.adm.model.entity.Room;
import com.project.edusync.adm.model.entity.Timeslot;
import com.project.edusync.adm.repository.RoomRepository;
import com.project.edusync.common.exception.BadRequestException;
import com.project.edusync.common.security.AuthUtil;
import com.project.edusync.em.model.dto.request.ExamAttendanceFinalizeRequestDTO;
import com.project.edusync.em.model.dto.request.ExamAttendanceMarkEntryDTO;
import com.project.edusync.em.model.dto.request.ExamAttendanceMarkRequestDTO;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExamAttendanceServiceTest {

    @Mock
    private AuthUtil authUtil;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private InvigilationRepository invigilationRepository;
    @Mock
    private SeatAllocationRepository seatAllocationRepository;
    @Mock
    private ExamAttendanceRepository examAttendanceRepository;
    @Mock
    private ExamScheduleRepository examScheduleRepository;
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private ExamAttendanceService examAttendanceService;

    @Test
    void finalizeAttendance_marksUnmarkedStudentsAbsent() {
        when(authUtil.getCurrentUserId()).thenReturn(100L);

        Staff staff = new Staff();
        staff.setId(55L);
        when(staffRepository.findByUserProfile_User_Id(100L)).thenReturn(Optional.of(staff));
        when(staffRepository.findById(55L)).thenReturn(Optional.of(staff));

        when(invigilationRepository.existsByExamScheduleIdAndRoom_IdAndStaffId(10L, 20L, 55L)).thenReturn(true);
        when(examAttendanceRepository.existsByExamScheduleIdAndRoomIdAndFinalizedTrue(10L, 20L)).thenReturn(false);
        when(seatAllocationRepository.findExamRoomStudentIds(10L, 20L)).thenReturn(List.of(1L, 2L, 3L));

        Room room = new Room();
        room.setId(20L);
        when(roomRepository.findById(20L)).thenReturn(Optional.of(room));

        ExamSchedule schedule = new ExamSchedule();
        schedule.setId(10L);
        schedule.setExamDate(LocalDate.now().minusDays(1));
        Timeslot timeslot = new Timeslot();
        timeslot.setEndTime(LocalTime.of(10, 0));
        schedule.setTimeslot(timeslot);
        when(examScheduleRepository.findByIdWithTimeslot(10L)).thenReturn(Optional.of(schedule));

        Student s2 = new Student();
        s2.setId(2L);
        Student s3 = new Student();
        s3.setId(3L);
        when(studentRepository.findAllById(anyCollection())).thenReturn(List.of(s2, s3));

        Student s1 = new Student();
        s1.setId(1L);
        ExamAttendance existing = ExamAttendance.builder()
            .id(500L)
            .examSchedule(schedule)
            .student(s1)
            .room(room)
            .status(ExamAttendanceStatus.PRESENT)
            .timestamp(LocalDateTime.now().minusMinutes(10))
            .finalized(false)
            .build();
        when(examAttendanceRepository.findByExamScheduleIdAndStudentIds(10L, List.of(1L, 2L, 3L))).thenReturn(List.of(existing));

        ExamAttendanceFinalizeRequestDTO request = new ExamAttendanceFinalizeRequestDTO();
        request.setExamScheduleId(10L);
        request.setRoomId(20L);

        var response = examAttendanceService.finalizeAttendance(request);

        assertEquals(3, response.getTotalStudents());
        assertEquals(1, response.getAlreadyMarked());
        assertEquals(2, response.getAutoMarkedAbsent());
        assertTrue(response.isFinalized());

        ArgumentCaptor<Iterable<ExamAttendance>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(examAttendanceRepository).saveAll(captor.capture());
        long autoAbsentCount = 0;
        for (ExamAttendance attendance : captor.getValue()) {
            if (attendance.getStatus() == ExamAttendanceStatus.ABSENT) {
                autoAbsentCount++;
            }
        }
        assertEquals(2L, autoAbsentCount);
    }

    @Test
    void markAttendance_rejectsWhenAlreadyFinalized() {
        when(authUtil.getCurrentUserId()).thenReturn(100L);

        Staff staff = new Staff();
        staff.setId(55L);
        when(staffRepository.findByUserProfile_User_Id(100L)).thenReturn(Optional.of(staff));
        when(invigilationRepository.existsByExamScheduleIdAndRoom_IdAndStaffId(10L, 20L, 55L)).thenReturn(true);
        when(examAttendanceRepository.existsByExamScheduleIdAndRoomIdAndFinalizedTrue(10L, 20L)).thenReturn(true);

        ExamAttendanceMarkEntryDTO entry = new ExamAttendanceMarkEntryDTO();
        entry.setStudentId(1L);
        entry.setStatus(ExamAttendanceStatus.PRESENT);

        ExamAttendanceMarkRequestDTO request = new ExamAttendanceMarkRequestDTO();
        request.setExamScheduleId(10L);
        request.setRoomId(20L);
        request.setEntries(List.of(entry));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> examAttendanceService.markAttendance(request));
        assertEquals("Attendance already finalized for this room", ex.getMessage());
    }

    @Test
    void finalizeAttendance_rejectsBeforeExamEnd() {
        when(authUtil.getCurrentUserId()).thenReturn(100L);

        Staff staff = new Staff();
        staff.setId(55L);
        when(staffRepository.findByUserProfile_User_Id(100L)).thenReturn(Optional.of(staff));
        when(invigilationRepository.existsByExamScheduleIdAndRoom_IdAndStaffId(10L, 20L, 55L)).thenReturn(true);
        when(examAttendanceRepository.existsByExamScheduleIdAndRoomIdAndFinalizedTrue(10L, 20L)).thenReturn(false);

        ExamSchedule schedule = new ExamSchedule();
        schedule.setId(10L);
        schedule.setExamDate(LocalDate.now());
        Timeslot timeslot = new Timeslot();
        timeslot.setEndTime(LocalTime.now().plusHours(1));
        schedule.setTimeslot(timeslot);
        when(examScheduleRepository.findByIdWithTimeslot(10L)).thenReturn(Optional.of(schedule));

        ExamAttendanceFinalizeRequestDTO request = new ExamAttendanceFinalizeRequestDTO();
        request.setExamScheduleId(10L);
        request.setRoomId(20L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> examAttendanceService.finalizeAttendance(request));
        assertEquals("Attendance can be finalized only after exam end time", ex.getMessage());
    }

    @Test
    void markAttendance_rejectsDuplicateStudentEntriesInSingleRequest() {
        when(authUtil.getCurrentUserId()).thenReturn(100L);

        Staff staff = new Staff();
        staff.setId(55L);
        when(staffRepository.findByUserProfile_User_Id(100L)).thenReturn(Optional.of(staff));
        when(invigilationRepository.existsByExamScheduleIdAndRoom_IdAndStaffId(10L, 20L, 55L)).thenReturn(true);
        when(examAttendanceRepository.existsByExamScheduleIdAndRoomIdAndFinalizedTrue(10L, 20L)).thenReturn(false);
        when(seatAllocationRepository.findExamRoomStudentIds(10L, 20L)).thenReturn(List.of(1L, 2L));

        ExamAttendanceMarkEntryDTO first = new ExamAttendanceMarkEntryDTO();
        first.setStudentId(1L);
        first.setStatus(ExamAttendanceStatus.PRESENT);

        ExamAttendanceMarkEntryDTO duplicate = new ExamAttendanceMarkEntryDTO();
        duplicate.setStudentId(1L);
        duplicate.setStatus(ExamAttendanceStatus.ABSENT);

        ExamAttendanceMarkRequestDTO request = new ExamAttendanceMarkRequestDTO();
        request.setExamScheduleId(10L);
        request.setRoomId(20L);
        request.setEntries(List.of(first, duplicate));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> examAttendanceService.markAttendance(request));
        assertEquals("Duplicate student entries are not allowed in one mark request", ex.getMessage());
    }
}
