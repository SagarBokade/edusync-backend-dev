ALTER TABLE exam_controller_assignment
    ADD COLUMN IF NOT EXISTS change_count INTEGER DEFAULT 0;
