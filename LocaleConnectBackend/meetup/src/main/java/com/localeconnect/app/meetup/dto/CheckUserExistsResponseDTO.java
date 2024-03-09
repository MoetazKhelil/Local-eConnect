package com.localeconnect.app.meetup.dto;

import com.localeconnect.app.meetup.error_handler.ErrorResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CheckUserExistsResponseDTO {
    private Boolean data;

    private String message;

    private ErrorResponse errors;

    private int status;
}