package com.localeconnect.app.trip.exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
