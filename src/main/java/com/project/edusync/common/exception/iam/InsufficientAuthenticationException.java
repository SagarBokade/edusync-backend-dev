package com.project.edusync.common.exception.iam;

import com.project.edusync.common.exception.EdusyncException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception indicating that an operation was attempted without proper authentication.
 * This replaces the standard Spring Security exception to align with our custom exception hierarchy.
 *
 * It results in an HTTP 401 Unauthorized response.
 */
public class InsufficientAuthenticationException extends EdusyncException {

    public InsufficientAuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

}