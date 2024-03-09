package com.localeconnect.app.trip.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripReviewDTO {
    private Long tripReviewId;
    private Long tripId;
    private Long userId;
    @Size(max = 1000, message = "The message is too long, exceeded 1000 characters")
    private String text;
    private LocalDateTime timestamp;

}
