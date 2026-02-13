package com.project.edusync.ams.model.exception;

/**
 * General exception for business logic errors during attendance processing (e.g., missing staff ID, data validation failures).
 */
public class AttendanceProcessingException extends RuntimeException {

    public AttendanceProcessingException() {
        super();
    }

    public AttendanceProcessingException(String message) {
        super(message);
    }

    public AttendanceProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttendanceProcessingException(Throwable cause) {
        super(cause);
    }
}