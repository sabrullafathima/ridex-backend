package com.project.ridex_backend.exception;

public class InvalidLoginDetailsException extends RuntimeException {
    public InvalidLoginDetailsException(String message) {
        super(message);
    }
}