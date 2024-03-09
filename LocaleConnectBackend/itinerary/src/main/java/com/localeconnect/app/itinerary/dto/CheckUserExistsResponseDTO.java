package com.localeconnect.app.itinerary.dto;

import com.localeconnect.app.itinerary.error_handler.ErrorResponse;
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