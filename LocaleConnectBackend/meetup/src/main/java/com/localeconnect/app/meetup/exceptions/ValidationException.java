package com.localeconnect.app.meetup.exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }
}
