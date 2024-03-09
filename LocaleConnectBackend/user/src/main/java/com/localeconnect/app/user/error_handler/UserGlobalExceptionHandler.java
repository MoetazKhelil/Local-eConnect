package com.localeconnect.app.user.error_handler;

import com.localeconnect.app.user.exception.LogicException;
import com.localeconnect.app.user.exception.UserAlreadyExistsException;
import com.localeconnect.app.user.exception.UserDoesNotExistException;
import com.localeconnect.app.user.exception.ValidationException;
import com.localeconnect.app.user.response_handler.ResponseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class UserGlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityNotFoundExceptions(Exception e){
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

    @ExceptionHandler(ValidationException.class)
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

    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<Object> handleLogicExceptions(
            Exception e
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        List<String> errorMessages = Arrays.asList(e.getMessage());
        System.out.println("UnauthorizedUser: " + errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                errorMessages
        );
        return ResponseHandler.generateResponse("Error!", status, null, errorResponse);
    }

    @ExceptionHandler(LogicException.class)
    public ResponseEntity<Object> handleItineraryAlreadyExistsExceptions(
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
