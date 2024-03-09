package com.localeconnect.app.meetup.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "meetup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "creator_id")
    private Long creatorId;
    private String name;
    private String description;
    private LocalDate date;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    private Double cost;
    private String location;

    @ElementCollection
    private List<String> spokenLanguages= new ArrayList<>();

    @ElementCollection
    private List<Long> meetupAttendees = new ArrayList<>();

    @Column
    private double ratingsTotal;

    @Column
    private int ratingsCount;

    @Column
    private double averageRating;
    public void addRating(double rating) {
        this.ratingsTotal += rating;
        this.ratingsCount++;
    }
    public void calcAverageRating() {
        this.averageRating = (this.ratingsCount > 0) ? this.ratingsTotal / this.ratingsCount : 0;
    }
}
