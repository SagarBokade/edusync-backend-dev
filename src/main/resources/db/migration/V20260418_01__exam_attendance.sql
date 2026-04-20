CREATE TABLE IF NOT EXISTS exam_attendance (
    id BIGSERIAL PRIMARY KEY,
    exam_schedule_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    status VARCHAR(32) NOT NULL,
    marked_by_staff_id BIGINT,
    marked_at TIMESTAMP NOT NULL DEFAULT now(),
    finalized BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uk_exam_attendance_schedule_student UNIQUE (exam_schedule_id, student_id),
    CONSTRAINT fk_exam_attendance_schedule FOREIGN KEY (exam_schedule_id) REFERENCES exam_schedule (id),
    CONSTRAINT fk_exam_attendance_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_exam_attendance_room FOREIGN KEY (room_id) REFERENCES rooms (id),
    CONSTRAINT fk_exam_attendance_marked_by FOREIGN KEY (marked_by_staff_id) REFERENCES staff (id)
);

CREATE INDEX IF NOT EXISTS idx_exam_attendance_schedule_room
    ON exam_attendance (exam_schedule_id, room_id);

CREATE INDEX IF NOT EXISTS idx_exam_attendance_marked_by
    ON exam_attendance (marked_by_staff_id);

