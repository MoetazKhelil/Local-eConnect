package com.localeconnect.app.notification.error_handler;

import com.localeconnect.app.notification.exceptions.ResourceNotFoundException;
import com.localeconnect.app.notification.exceptions.ValidationException;
import com.localeconnect.app.notification.response_handler.ResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class NotificationGlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404

        List<String> errorMessages = Arrays.asList(e.getMessage());
        log.error("Custom ResourceNotFoundException: " + errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errorMessages
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationErrors(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream().map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage()).collect(Collectors.toList());

        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        log.error("Custom MethodArgumentNotValidException: " + errors);
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);

    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleBadRequestExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        log.error("Custom ValidationException: " + e.getMessage());
        log.error("Custom ValidationException TRACE: " + Arrays.toString(e.getStackTrace()));

        List<String> errors = Arrays.asList(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(Exception.class) // exception handled
    public ResponseEntity<Object> handleInternalServerErrorExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500

        log.error("Custom General Exception: " + e.getMessage());
        log.error("Custom General Exception TRACE: " + Arrays.toString(e.getStackTrace()));

        List<String> errors = Arrays.asList("Internal Server Error: Something Went Wrong!");

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

}
