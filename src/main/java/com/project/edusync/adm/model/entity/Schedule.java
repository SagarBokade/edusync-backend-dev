package com.project.edusync.adm.model.entity;

import com.project.edusync.adm.model.enums.ScheduleStatus;
import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.uis.model.entity.details.TeacherDetails;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a single scheduled class in the timetable.
 * This is the central "fact" table connecting all dimensions.
 *
 * This entity extends AuditableEntity to gain ID (Long), UUID,
 * and audit timestamp fields.
 *
 * Relationships will be joined using the inherited 'id' (Long) primary key.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true) // Includes inherited 'id' and 'uuid'
@ToString(callSuper = true, exclude = {"section", "subject", "teacher", "room", "timeslot"}) // Exclude lazy fields
@Entity
@Table(name = "schedule")
public class Schedule extends AuditableEntity {

    // The @Id (Long id) and 'uuid' are inherited from AuditableEntity.

    @Column(name = "status", length = 50)
    private ScheduleStatus status;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- Relationships ---
    // These are all Many-to-One, as many schedule entries can
    // point to one section, one subject, etc.

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false) // This joins on the 'id' column of the 'sections' table
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false) // This joins on the 'id' column of the 'subjects' table
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private TeacherDetails teacher;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false) // This joins on the 'id' column of the 'rooms' table
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timeslot_id", nullable = false) // This joins on the 'id' column of the 'timeslots' table
    private Timeslot timeslot;

}
