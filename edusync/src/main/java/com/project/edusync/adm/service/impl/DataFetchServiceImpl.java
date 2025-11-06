package com.project.edusync.adm.service.impl;

import com.project.edusync.adm.model.dto.response.AvailableRoomDto;
import com.project.edusync.adm.model.dto.response.AvailableSubjectDto;
import com.project.edusync.adm.model.dto.response.AvailableTeacherDto;
import com.project.edusync.adm.model.entity.AcademicConstraint;
import com.project.edusync.adm.model.entity.Room;
import com.project.edusync.adm.model.entity.Section;
import com.project.edusync.adm.model.entity.Subject;
import com.project.edusync.adm.repository.RoomRepository;
import com.project.edusync.adm.repository.SectionRepository;
import com.project.edusync.adm.service.DataFetchService;
import com.project.edusync.uis.model.entity.details.TeacherDetails; // Assumed path
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true) // Service is read-only by default
public class DataFetchServiceImpl implements DataFetchService {

    private final com.project.edusync.uis.repository.details.TeacherDetailsRepository teacherDetailsRepository;
    private final RoomRepository roomRepository;
    private final SectionRepository sectionRepository;

    @Override
    public List<AvailableTeacherDto> getAvailableTeachers(UUID subjectId) {
        log.info("Fetching qualified teachers for subject {} based on schedule history", subjectId);

        // Call the updated repository method
        List<TeacherDetails> teachers = teacherDetailsRepository
                .findQualifiedTeachersForSubject(subjectId);

        return teachers.stream()
                .map(this::toAvailableTeacherDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailableRoomDto> getAvailableRooms(UUID timeslotId, String roomType) {
        log.info("Fetching available rooms for timeslot {} (Type: {})", timeslotId, roomType);
        List<Room> rooms;
        if (roomType != null && !roomType.isEmpty()) {
            rooms = roomRepository.findAvailableRoomsByType(timeslotId, roomType);
        } else {
            rooms = roomRepository.findAvailableRooms(timeslotId);
        }

        return rooms.stream()
                .map(this::toAvailableRoomDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AvailableSubjectDto> getAvailableSubjects(UUID sectionId) {
        log.info("Fetching available subjects for section {}", sectionId);
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> {
                    log.warn("Section with id {} not found", sectionId);
                    return new RuntimeException("No section resource found with id: " + sectionId);
                });

        // NOTE: This part still uses AcademicConstraint.
        // If you also want to change this, you'll need to define a new business logic,
        // as this is the only link between a Section and its Subjects.
        return section.getAcademicConstraints().stream()
                .map(AcademicConstraint::getSubject)
                .filter(subject -> subject != null && subject.getIsActive()) // Added null check
                .distinct() // Ensure unique subjects
                .map(this::toAvailableSubjectDto)
                .collect(Collectors.toList());
    }

    // --- Private DTO Builder Methods ---

    private AvailableTeacherDto toAvailableTeacherDto(TeacherDetails entity) {
        return AvailableTeacherDto.builder()
                .id(entity.getId()) // Using Long id
                .name(entity.getStaff().getUserProfile().getFirstName()) // Assumed path to name
                .build();
    }

    private AvailableRoomDto toAvailableRoomDto(Room entity) {
        return AvailableRoomDto.builder()
                .uuid(entity.getUuid())
                .name(entity.getName())
                .build();
    }

    private AvailableSubjectDto toAvailableSubjectDto(Subject entity) {
        return AvailableSubjectDto.builder()
                .uuid(entity.getUuid())
                .name(entity.getName())
                .build();
    }
}