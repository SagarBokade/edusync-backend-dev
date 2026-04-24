package com.project.edusync.em.model.service;

import com.project.edusync.common.security.AuthUtil;
import com.project.edusync.em.model.repository.ExamControllerAssignmentRepository;
import com.project.edusync.em.model.repository.ExamRepository;
import com.project.edusync.em.model.repository.ExamScheduleRepository;
import com.project.edusync.em.model.repository.InvigilationRepository;
import com.project.edusync.iam.model.entity.User;
import com.project.edusync.uis.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component("examControllerAccess")
@RequiredArgsConstructor
public class ExamControllerAccessService {

    private static final Set<String> ADMIN_ROLES = Set.of("ROLE_ADMIN", "ROLE_SCHOOL_ADMIN", "ROLE_SUPER_ADMIN");

    private final AuthUtil authUtil;
    private final StaffRepository staffRepository;
    private final ExamRepository examRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final InvigilationRepository invigilationRepository;
    private final ExamControllerAssignmentRepository assignmentRepository;

    public boolean canAccessExam(Long examId) {
        if (examId == null) {
            return false;
        }
        User user = authUtil.getCurrentUser();
        if (hasAdminRole(user)) {
            return true;
        }
        Long staffId = staffRepository.findByUserProfile_User_Id(user.getId())
            .map(s -> s.getId())
            .orElse(null);
        if (staffId == null) {
            return false;
        }
        return assignmentRepository.existsByExamIdAndStaffIdAndActiveTrue(examId, staffId);
    }

    public boolean canAccessExamUuid(UUID examUuid) {
        if (examUuid == null) {
            return false;
        }
        return examRepository.findIdByUuid(examUuid)
            .map(this::canAccessExam)
            .orElse(false);
    }

    public boolean canAccessSchedule(Long scheduleId) {
        if (scheduleId == null) {
            return false;
        }
        return examScheduleRepository.findExamIdByScheduleId(scheduleId)
            .map(this::canAccessExam)
            .orElse(false);
    }

    public boolean canAccessInvigilation(Long invigilationId) {
        if (invigilationId == null) {
            return false;
        }
        return invigilationRepository.findExamIdByInvigilationId(invigilationId)
            .map(this::canAccessExam)
            .orElse(false);
    }

    private boolean hasAdminRole(User user) {
        return user.getRoles().stream().anyMatch(role -> ADMIN_ROLES.contains(role.getName()));
    }
}

