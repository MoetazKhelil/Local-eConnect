package com.localeconnect.app.notification.response_handler;

import com.localeconnect.app.notification.error_handler.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj, ErrorResponse error) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObj);
        map.put("errors", error);
        return new ResponseEntity<>(map, status);
    }
}
