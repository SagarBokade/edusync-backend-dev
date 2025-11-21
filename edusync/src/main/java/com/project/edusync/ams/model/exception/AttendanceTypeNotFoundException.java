package com.project.edusync.ams.model.exception;

/**
 * Custom exception thrown when a specific AttendanceType (by ID or ShortCode) cannot be found.
 */
public class AttendanceTypeNotFoundException extends RuntimeException {
    public AttendanceTypeNotFoundException(String identifier) {
        super("Attendance Type not found with identifier: " + identifier);
    }
}