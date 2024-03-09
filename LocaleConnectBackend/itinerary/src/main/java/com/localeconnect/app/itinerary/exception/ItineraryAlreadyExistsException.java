package com.localeconnect.app.itinerary.exception;

public class ItineraryAlreadyExistsException extends RuntimeException {
    public ItineraryAlreadyExistsException(String message) {
        super(message);
    }
}
