package com.localeconnect.app.itinerary.dto;

import com.localeconnect.app.itinerary.error_handler.ErrorResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GCPResponseDTO {

    private String data;

    private String message;

    private ErrorResponse errors;

    private int status;
}
