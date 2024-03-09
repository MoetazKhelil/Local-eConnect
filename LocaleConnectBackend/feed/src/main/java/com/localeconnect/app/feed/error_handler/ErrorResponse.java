package com.localeconnect.app.feed.error_handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    // customizing timestamp serialization format
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;

    private int code;

    private String status;

    private List<String> errors;


    public ErrorResponse() {
        timestamp = new Date();
    }

    public ErrorResponse(
            HttpStatus httpStatus,
            List<String> errorMessages
    ) {
        this();

        this.code = httpStatus.value();
        this.status = httpStatus.name();
        this.errors = errorMessages ;
    }

}