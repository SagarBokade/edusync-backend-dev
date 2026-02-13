package com.project.edusync.ams.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested attendance record (staff or student) is not found.
 * Automatically maps to HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AttendanceRecordNotFoundException extends RuntimeException {

    public AttendanceRecordNotFoundException() {
        super();
    }

    public AttendanceRecordNotFoundException(String message) {
        super(message);
    }

    public AttendanceRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
