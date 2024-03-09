package com.localeconnect.app.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {

    private Long id;

    private Long localguideId;

    @NotBlank(message = "Trip name is required")
    private String name;

    private String description;

    @FutureOrPresent(message = "Departure time cannot be in the past")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate departureTime;

    @NotBlank(message = "Destination is required")
    private String destination;

    @NotNull(message = "Duration is required")
    private Integer durationInHours;

    @Positive(message = "A trip must have at least one traveler")
    private Integer capacity;

    @NotEmpty(message = "At least one traveler must be in the trip")
    @ElementCollection
    private List<Long> travelers = new ArrayList<>();

    @NotEmpty(message = "At least one language must be specified")
    @ElementCollection
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    private List<String> dailyActivities = new ArrayList<>();

    @ElementCollection
    private List<String> placesToVisit = new ArrayList<>();

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    private double ratingsTotal;
    private int ratingsCount;
    @Min(value = 0)
    @Max(value = 5)
    private double averageRating;
    private List<Long> tripAttendees = new ArrayList<>();

    public void calcAverageRating() {
        this.averageRating = (this.ratingsCount > 0) ? this.ratingsTotal / this.ratingsCount : 0;
    }

}
