package com.project.edusync.em.model.entity;

import com.project.edusync.uis.model.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Time-bound seat allocation. A seat is considered unavailable if:
 *   existing.startTime < new.endTime AND existing.endTime > new.startTime
 *
 * startTime/endTime are denormalized from ExamSchedule for indexed overlap queries.
 * Old allocations are NEVER deleted — availability is derived purely from time logic.
 *
 * BENCH SHARING: Multiple students may share the same seat for the same schedule,
 * up to ExamSchedule.maxStudentsPerSeat. The unique constraint now includes
 * student_id to allow this.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "seat_allocation",
    uniqueConstraints = {
        // Changed: seat+schedule is no longer unique — seat+schedule+student is unique
        // This allows multiple students on the same seat for the same schedule
        @UniqueConstraint(name = "uk_seat_alloc_seat_schedule_student",
            columnNames = {"seat_id", "exam_schedule_id", "student_id"}),
        // A student still can only have ONE seat per schedule
        @UniqueConstraint(name = "uk_seat_alloc_student_schedule",
            columnNames = {"student_id", "exam_schedule_id"})
    },
    indexes = {
        @Index(name = "idx_seat_alloc_overlap",
            columnList = "seat_id, start_time, end_time"),
        @Index(name = "idx_seat_alloc_schedule",
            columnList = "exam_schedule_id"),
        @Index(name = "idx_seat_alloc_student_time",
            columnList = "student_id, start_time, end_time")
    })
public class SeatAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_schedule_id", nullable = false)
    private ExamSchedule examSchedule;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
