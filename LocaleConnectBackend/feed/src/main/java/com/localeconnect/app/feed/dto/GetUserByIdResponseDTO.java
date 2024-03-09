package com.localeconnect.app.feed.dto;

import com.localeconnect.app.feed.error_handler.ErrorResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetUserByIdResponseDTO {
    private UserFeedDTO data;
    private String message;
    private ErrorResponse errors;
    private int status;
}