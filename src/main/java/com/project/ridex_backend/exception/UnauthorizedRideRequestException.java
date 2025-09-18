package com.project.ridex_backend.exception;

public class UnauthorizedRideRequestException extends RuntimeException {
    public UnauthorizedRideRequestException(String message) {
        super(message);
    }
}
