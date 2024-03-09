package com.localeconnect.app.feed.error_handler;

import com.localeconnect.app.feed.exceptions.LogicException;
import com.localeconnect.app.feed.exceptions.ResourceNotFoundException;
import com.localeconnect.app.feed.exceptions.ValidationException;
import com.localeconnect.app.feed.response_handler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class FeedGlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404

        List<String> errorMessages = Arrays.asList(e.getMessage());
        System.out.println("Custom ResourceNotFoundException: " + errorMessages);

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

        System.out.println("Custom MethodArgumentNotValidException: " + errors);
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
        System.out.println("Custom ValidationException: " + e.getMessage());
        System.out.println("Custom ValidationException TRACE: " + Arrays.toString(e.getStackTrace()));

        List<String> errors = Arrays.asList(e.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<Object> handleLogicExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        List<String> errorMessages = Arrays.asList(e.getMessage());
        System.out.println("LogicException: " + errorMessages);

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
        System.out.println(e);
//        System.out.println("Custom General Exception: " + e.getMessage());
//        System.out.println("Custom General Exception TRACE: " + Arrays.toString(e.getStackTrace()));

        List<String> errors = Arrays.asList("Internal Server Error: Something Went Wrong!");

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errors
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

}
