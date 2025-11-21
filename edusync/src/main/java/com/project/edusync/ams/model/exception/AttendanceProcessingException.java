package com.project.edusync.ams.model.exception;

/**
 * General exception for business logic errors during attendance processing (e.g., missing staff ID, data validation failures).
 */
public class AttendanceProcessingException extends RuntimeException {
    public AttendanceProcessingException(String message) {
        super(message);
    }
}