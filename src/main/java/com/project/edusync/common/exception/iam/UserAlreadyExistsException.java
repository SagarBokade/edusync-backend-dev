package com.project.edusync.common.exception.iam;

import com.project.edusync.common.exception.EdusyncException;
import org.springframework.http.HttpStatus;

/**
 * Thrown when attempting to register a user with a username or email
 * that is already occupied.
 */
public class UserAlreadyExistsException extends EdusyncException {
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}