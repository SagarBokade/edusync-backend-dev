package com.project.edusync.ams.model.exception;

/**
 * Custom exception thrown when a submitted attendance short code (e.g., 'X') does not map to a valid AttendanceType in the database.
 */
public class InvalidAttendanceTypeException extends RuntimeException {
    public InvalidAttendanceTypeException(String message) {
        super(message);
    }
}