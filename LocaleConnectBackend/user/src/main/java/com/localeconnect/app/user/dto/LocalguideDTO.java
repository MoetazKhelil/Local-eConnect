package com.localeconnect.app.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class LocalguideDTO extends UserDTO {
    private String city;
    private double ratingsTotal;
    private int ratingsCount;
    @Min(value = 0)
    @Max(value = 5)
    private double averageRating;
    private final boolean registeredAsLocalGuide = true;
    private double rating;
    public LocalguideDTO() {
        super();
    }

    public void addRating(double rating) {
        this.ratingsTotal += rating;
        this.ratingsCount++;
    }

    public void calcAverageRating() {
        this.averageRating = (this.ratingsCount > 0) ? this.ratingsTotal / this.ratingsCount : 0;
    }
}

