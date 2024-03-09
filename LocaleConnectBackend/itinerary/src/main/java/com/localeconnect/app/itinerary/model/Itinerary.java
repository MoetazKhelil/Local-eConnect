package com.localeconnect.app.itinerary.model;

import com.localeconnect.app.itinerary.dto.Tag;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itinerary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    private String name;
    private String description;
    private Integer numberOfDays;

//    @ElementCollection(targetClass = Tag.class)
//    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable(name = "itinerary_tags")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    private List<String> placesToVisit = new ArrayList<>();

    @ElementCollection
    private List<String> dailyActivities = new ArrayList<>();

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    @Column
    private double ratingsTotal;

    @Column
    private int ratingsCount;

    @Column
    private double averageRating;

    @ElementCollection
    private List<Long> itineraryAttendees = new ArrayList<>();
    public void addRating(double rating) {
        this.ratingsTotal += rating;
        this.ratingsCount++;
    }
    public void calcAverageRating() {
        this.averageRating = (this.ratingsCount > 0) ? this.ratingsTotal / this.ratingsCount : 0;
    }
}


