package com.localeconnect.app.trip.response_handler;

import com.localeconnect.app.trip.error_handler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status,
                                                   Object responseObject, ErrorResponse error) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObject);
        map.put("errors", error);
        return new ResponseEntity<Object>(map, status);
    }
}
