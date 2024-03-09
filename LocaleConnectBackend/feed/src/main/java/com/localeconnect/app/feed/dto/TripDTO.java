package com.localeconnect.app.feed.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.persistence.ElementCollection;
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

    private Long localguideId;

    @NotBlank(message = "Trip name is required")
    private String name;
    @NotBlank(message = "This is a required field")
    private String description;

    @FutureOrPresent(message = "Departure time cannot be in the past")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
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

    @NotEmpty(message = "At least one place to visit must be specified")
    @ElementCollection
    private List<String> placesToVisit = new ArrayList<>();

    @ElementCollection
    private List<String> dailyActivities = new ArrayList<>();

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

}
