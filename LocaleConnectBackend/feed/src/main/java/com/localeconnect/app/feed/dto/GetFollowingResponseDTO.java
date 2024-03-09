package com.localeconnect.app.feed.dto;

import com.localeconnect.app.feed.error_handler.ErrorResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class GetFollowingResponseDTO {
    private List<UserFeedDTO> data;
    private String message;
    private ErrorResponse errors;
    private int status;
}