package com.project.edusync.common.exception;

public class EdusyncException extends RuntimeException {
    public EdusyncException(String message) {
        //This is the base exception
        super(message);
    }
}
