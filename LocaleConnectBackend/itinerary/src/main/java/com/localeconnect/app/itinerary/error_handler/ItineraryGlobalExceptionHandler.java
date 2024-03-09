package com.localeconnect.app.itinerary.error_handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localeconnect.app.itinerary.dto.GCPResponseDTO;
import com.localeconnect.app.itinerary.exception.*;
import com.localeconnect.app.itinerary.response_handler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ItineraryGlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, ItineraryNotFoundException.class, ReviewNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundExceptions(Exception e) {
        HttpStatus status = HttpStatus.NOT_FOUND; //404
        List<String> errorMessages = Arrays.asList(e.getMessage());
        System.out.println("ResourceNotFoundException: " + errorMessages);

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

        System.out.println("MethodArgumentNotValidException: " + errors);
        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);

    }

    @ExceptionHandler(ReviewValidationException.class)
    public ResponseEntity<Object> handleBadRequestExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400
        System.out.println("ValidationException: " + e.getMessage());

        List<String> errors = Arrays.asList(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<Object> handleLogicExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        List<String> errorMessages = Arrays.asList(e.getMessage());
        System.out.println("UnauthorizedUserException: " + errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errorMessages
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(ItineraryAlreadyExistsException.class)
    public ResponseEntity<Object> handleItineraryAlreadyExistsExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        List<String> errorMessages = Arrays.asList(e.getMessage());
        System.out.println("ItineraryAlreadyExistsException: " + errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errorMessages
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(GCPException.class)
    public ResponseEntity<Object> handleGCPException(
            Exception e
    ) {
        System.out.println(e);
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        List<String> errorMessages = Arrays.asList(e.getMessage());
        System.out.println("ItineraryAlreadyExistsException: " + errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errorMessages
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }


    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Object> handleWebClientException(
            WebClientResponseException e
    ) throws JsonProcessingException {
        System.out.println("BODY " + e.getResponseBodyAsString());
        String errorMsg = e.getResponseBodyAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        GCPResponseDTO gcpResponseDTO = objectMapper.readValue(errorMsg, GCPResponseDTO.class);
        System.out.println(gcpResponseDTO.getErrors());
        HttpStatus status = HttpStatus.valueOf(gcpResponseDTO.getStatus()); // 400

        List<String> errorMessages = gcpResponseDTO.getErrors().getErrors();
        System.out.println("Error in GCP: " + errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errorMessages
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(Exception.class) // exception handled
    public ResponseEntity<Object> handleInternalServerErrorExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        System.out.println("General Exception: " + e.getMessage());
        System.out.println("Stack Trace: " + Arrays.toString(e.getStackTrace()));

        List<String> errors = Arrays.asList("Internal Server Error: Something Went Wrong!");

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }
}
