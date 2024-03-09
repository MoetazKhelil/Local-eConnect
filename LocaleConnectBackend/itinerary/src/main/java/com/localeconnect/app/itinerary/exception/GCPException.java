package com.localeconnect.app.itinerary.exception;

import com.localeconnect.app.itinerary.dto.GCPResponseDTO;

import java.util.List;

public class GCPException extends RuntimeException {

    public GCPException(String message) {
        super(message);
    }

    public GCPException(GCPResponseDTO response) {
        super(response.getMessage());
        System.out.println(response);
    }

    public GCPException() {

    }
}
