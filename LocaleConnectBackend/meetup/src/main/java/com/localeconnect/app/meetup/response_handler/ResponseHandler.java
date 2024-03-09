package com.localeconnect.app.meetup.response_handler;

import com.localeconnect.app.meetup.error_handler.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj, ErrorResponse error) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", message);
        map.put("status", status.value());
        map.put("data", responseObj);
        map.put("errors", error);

        return new ResponseEntity<Object>(map, status);
    }
}
