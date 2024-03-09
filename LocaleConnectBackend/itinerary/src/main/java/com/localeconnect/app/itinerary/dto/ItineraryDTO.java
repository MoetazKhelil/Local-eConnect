package com.localeconnect.app.itinerary.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ItineraryDTO {
    private Long id;
    private Long userId;
    @NotBlank(message = "This is a required field")
    private String name;
    private String description;
    @Positive(message = "Number of days must be greater than or equal to 1")
    private Integer numberOfDays;
    private List<String> tags;
    @NotEmpty(message = "Provide at least one destination")
    private List<String> placesToVisit;
    private List<String> dailyActivities;
    private List<String> imageUrls;
    private double ratingsTotal;
    private int ratingsCount;
    @Min(value = 0)
    @Max(value = 5)
    private double averageRating;
    private List<Long> itineraryAttendees = new ArrayList<>();

    public void calcAverageRating() {
        this.averageRating = (this.ratingsCount > 0) ? this.ratingsTotal / this.ratingsCount : 0;
    }

}
