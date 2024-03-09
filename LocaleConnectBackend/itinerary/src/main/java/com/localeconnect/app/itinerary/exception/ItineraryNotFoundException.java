package com.localeconnect.app.itinerary.exception;

public class ItineraryNotFoundException extends RuntimeException {
    public ItineraryNotFoundException(String message) {
        super(message);
    }
}
