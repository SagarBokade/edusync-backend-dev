package com.project.edusync.ams.model.service.implementation;

import com.project.edusync.ams.model.dto.request.StaffAttendanceRequestDTO;
import com.project.edusync.ams.model.dto.response.StaffAttendanceResponseDTO;
import com.project.edusync.ams.model.entity.AttendanceType;
import com.project.edusync.ams.model.entity.StaffDailyAttendance;
import com.project.edusync.ams.model.enums.AttendanceSource;
import com.project.edusync.ams.model.exception.AttendanceProcessingException;
import com.project.edusync.ams.model.exception.AttendanceRecordNotFoundException;
import com.project.edusync.ams.model.repository.AttendanceTypeRepository;
import com.project.edusync.ams.model.repository.StaffDailyAttendanceRepository;
import com.project.edusync.ams.model.service.StaffAttendanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffAttendanceServiceImpl implements StaffAttendanceService {

    private final StaffDailyAttendanceRepository repo;
    private final AttendanceTypeRepository attendanceTypeRepo;

    /* -------------------------------------------------------------
     * CREATE / UPSERT
     * ------------------------------------------------------------- */
    @Override
    @Transactional
    public StaffAttendanceResponseDTO createAttendance(StaffAttendanceRequestDTO req, Long performedBy) {

        validateTimes(req.getTimeIn(), req.getTimeOut());

        AttendanceType at = attendanceTypeRepo.findByShortCodeIgnoreCase(req.getAttendanceShortCode())
                .orElseThrow(() ->
                        new AttendanceProcessingException("Invalid attendance short code: " + req.getAttendanceShortCode())
                );

        Optional<StaffDailyAttendance> existing =
                repo.findByStaffIdAndAttendanceDate(req.getStaffId(), req.getAttendanceDate());

        StaffDailyAttendance e = existing.orElseGet(StaffDailyAttendance::new);
        e.setStaffId(req.getStaffId());
        e.setAttendanceDate(req.getAttendanceDate());
        e.setAttendanceType(at);
        e.setTimeIn(req.getTimeIn());
        e.setTimeOut(req.getTimeOut());
        e.setTotalHours(req.getTotalHours());
        e.setSource(req.getSource());
        e.setNotes(req.getNotes());

        StaffDailyAttendance saved = repo.save(e);
        return toDto(saved);
    }

    /* -------------------------------------------------------------
     * BULK CREATE / UPSERT
     * ------------------------------------------------------------- */
    @Override
    @Transactional
    public List<StaffAttendanceResponseDTO> bulkCreate(List<StaffAttendanceRequestDTO> requests, Long performedBy) {
        if (requests == null || requests.isEmpty()) return Collections.emptyList();

        Set<String> codes = requests.stream()
                .map(r -> r.getAttendanceShortCode().trim().toUpperCase())
                .collect(Collectors.toSet());

        Map<String, AttendanceType> types = new HashMap<>();
        for (String code : codes) {
            attendanceTypeRepo.findByShortCodeIgnoreCase(code)
                    .ifPresent(t -> types.put(code, t));
        }

        Set<String> missing = new HashSet<>(codes);
        missing.removeAll(types.keySet());
        if (!missing.isEmpty()) {
            throw new AttendanceProcessingException("Unknown attendance type(s): " + missing);
        }

        List<StaffDailyAttendance> saved = new ArrayList<>();

        for (StaffAttendanceRequestDTO r : requests) {
            validateTimes(r.getTimeIn(), r.getTimeOut());

            AttendanceType at = types.get(r.getAttendanceShortCode().trim().toUpperCase());
            Optional<StaffDailyAttendance> existing =
                    repo.findByStaffIdAndAttendanceDate(r.getStaffId(), r.getAttendanceDate());

            StaffDailyAttendance e = existing.orElseGet(StaffDailyAttendance::new);
            e.setStaffId(r.getStaffId());
            e.setAttendanceDate(r.getAttendanceDate());
            e.setAttendanceType(at);
            e.setTimeIn(r.getTimeIn());
            e.setTimeOut(r.getTimeOut());
            e.setTotalHours(r.getTotalHours());
            e.setSource(r.getSource());
            e.setNotes(r.getNotes());

            saved.add(repo.save(e));
        }

        return saved.stream().map(this::toDto).toList();
    }

    /* -------------------------------------------------------------
     * LIST FILTERED
     * ------------------------------------------------------------- */
    @Override
    public Page<StaffAttendanceResponseDTO> listAttendances(Pageable pageable,
                                                            Optional<Long> staffId,
                                                            Optional<LocalDate> date) {

        Page<StaffDailyAttendance> page = repo.findAll(pageable);

        List<StaffDailyAttendance> filtered = page.getContent().stream()
                .filter(e -> staffId.map(id -> id.equals(e.getStaffId())).orElse(true))
                .filter(e -> date.map(d -> d.equals(e.getAttendanceDate())).orElse(true))
                .toList();

        return new PageImpl<>(
                filtered.stream().map(this::toDto).toList(),
                pageable,
                page.getTotalElements()
        );
    }

    /* -------------------------------------------------------------
     * GET ONE
     * ------------------------------------------------------------- */
    @Override
    public StaffAttendanceResponseDTO getAttendance(Long id) {
        StaffDailyAttendance e = repo.findById(id)
                .orElseThrow(() ->
                        new AttendanceRecordNotFoundException("Staff attendance not found: " + id)
                );
        return toDto(e);
    }

    /* -------------------------------------------------------------
     * UPDATE
     * ------------------------------------------------------------- */
    @Override
    @Transactional
    public StaffAttendanceResponseDTO updateAttendance(Long id, StaffAttendanceRequestDTO req, Long performedBy) {

        StaffDailyAttendance e = repo.findById(id)
                .orElseThrow(() -> new AttendanceRecordNotFoundException("Record not found: " + id));

        if (!e.getStaffId().equals(req.getStaffId()) ||
                !e.getAttendanceDate().equals(req.getAttendanceDate())) {
            throw new AttendanceProcessingException("Cannot change staffId or attendanceDate");
        }

        AttendanceType at = attendanceTypeRepo.findByShortCodeIgnoreCase(req.getAttendanceShortCode())
                .orElseThrow(() -> new AttendanceProcessingException("Invalid short code"));

        validateTimes(req.getTimeIn(), req.getTimeOut());

        e.setAttendanceType(at);
        e.setTimeIn(req.getTimeIn());
        e.setTimeOut(req.getTimeOut());
        e.setTotalHours(req.getTotalHours());
        e.setSource(req.getSource());
        e.setNotes(req.getNotes());

        return toDto(repo.save(e));
    }

    /* -------------------------------------------------------------
     * DELETE
     * ------------------------------------------------------------- */
    @Override
    @Transactional
    public void deleteAttendance(Long id, Long performedBy) {
        repo.deleteById(id);
    }

    /* -------------------------------------------------------------
     * HELPERS
     * ------------------------------------------------------------- */
    private void validateTimes(LocalTime in, LocalTime out) {
        if (in != null && out != null && in.isAfter(out)) {
            throw new AttendanceProcessingException("timeIn cannot be after timeOut");
        }
    }

    /**
     * Convert entity → DTO using your EXACT DTO constructor order.
     */
    private StaffAttendanceResponseDTO toDto(StaffDailyAttendance e) {

        String attendanceMark = null;
        String shortCode = null;
        String colorCode = null;

        if (e.getAttendanceType() != null) {
            attendanceMark  = e.getAttendanceType().getTypeName();
            shortCode       = e.getAttendanceType().getShortCode();
            colorCode       = e.getAttendanceType().getColorCode();
        }

        return new StaffAttendanceResponseDTO(
                e.getId(),                    // staffAttendanceId
                e.getStaffId(),               // staffId
                null,                         // staffName (not from DB)
                null,                         // jobTitle
                e.getAttendanceDate(),
                attendanceMark,
                shortCode,
                colorCode,
                e.getTimeIn(),
                e.getTimeOut(),
                e.getTotalHours(),
                e.getSource(),
                e.getNotes()
        );
    }
}
