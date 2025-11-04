package com.project.edusync.adm.model.entity;

import com.project.edusync.common.model.AuditableEntity;
import com.project.edusync.uis.model.entity.details.TeacherDetails;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true) // Includes inherited 'id' and 'uuid' in calculations
@ToString(callSuper = true, exclude = {"teacher", "subject", "section", "timeslot"}) // Exclude lazy fields
public class AcademicConstraint extends AuditableEntity {

    @Column(name = "constraint_type", nullable = false, length = 100)
    private String constraintType;

    @Column(name = "value_string")
    private String valueString;

    @Column(name = "value_int")
    private Integer valueInt;

    @Column(name = "is_hard_constraint", nullable = false)
    private boolean isHardConstraint;

    @Column(name = "weight_int")
    private Integer weightInt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // --- Relationships ---
    // These are all Many-to-One, as many constraints can point to one teacher, subject, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private TeacherDetails teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id") // This joins on the 'id' column of the 'subjects' table
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id") // This joins on the 'id' column of the 'sections' table
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id") // This joins on the 'id' column of the 'timeslots' table
    private Timeslot timeslot;
}
