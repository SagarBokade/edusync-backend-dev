package com.project.edusync.ams.model.exception;

/**
 * Custom exception thrown when a specific attendance record (by ID) cannot be found.
 */
public class AttendanceRecordNotFoundException extends RuntimeException {
    public AttendanceRecordNotFoundException(String message) {
        super(message);
    }
}