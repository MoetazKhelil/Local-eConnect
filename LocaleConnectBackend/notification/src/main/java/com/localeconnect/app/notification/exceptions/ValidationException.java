package com.localeconnect.app.notification.exceptions;

public class ValidationException extends RuntimeException{
    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }
}
