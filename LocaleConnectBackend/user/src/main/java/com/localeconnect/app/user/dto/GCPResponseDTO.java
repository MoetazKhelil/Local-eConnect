package com.localeconnect.app.user.dto;

import com.localeconnect.app.user.error_handler.ErrorResponse;
import lombok.*;

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
